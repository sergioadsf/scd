package br.com.conectasol.scd.elastic.service.folha;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;

import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;
import br.com.conectasol.scd.elastic.mapping.Folha;
import br.com.conectasol.scd.elastic.service.AbsElasticService;
import br.com.conectasol.scd.elastic.service.BulkBuilder;
import br.com.conectasol.scd.util.PathProperties;

@Service
public class FolhaService extends AbsElasticService {

	private int SIZE_DEFAULT = 2000;

	@Autowired
	private PathProperties prop;

	private static AtomicInteger at = new AtomicInteger(0);

	@PostConstruct
	public void init() {
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
	}

	private List<Folha> read(String anomes) throws IOException {
		String p = String.format("%sFolhaPagamento_%s.csv", prop.getCsv(), anomes);

		return doRead(p);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Folha> doRead(String p) throws IOException {
		try (Reader reader = Files.newBufferedReader(Paths.get(p), Charset.forName("UTF-8"));) {
			return new CsvToBeanBuilder(reader).withType(Folha.class).withSeparator(';')
					.withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withSkipLines(0).build().parse();
		}
	}

	public void indexar(String anomes) {
		RestClient client = null;
		try {
			client = this.openConnection();
			MIndex mIndex = Folha.class.getAnnotation(MIndex.class);
			String name = mIndex.name();

			StringBuilder jBuilder = new StringBuilder();

			jBuilder.append(read(anomes)
					.parallelStream().map(folha -> BulkBuilder.init(name).addId(UUID.randomUUID().toString())
							.addComando("index").addJson(this.prepareJson(folha)).build())
					.collect(Collectors.joining()));

			this.doBulk(client, jBuilder);
			System.out.println("FIM");
		} catch (Exception e) {
			e.printStackTrace();
			this.close(client);
		}
	}

	public String consultar(String nome, int tamanho) throws IOException {
		RestClient client = null;
		try {
			client = this.openConnection();
			MIndex mIndex = Folha.class.getAnnotation(MIndex.class);
			StringBuilder jBuilder = new StringBuilder("{");
			jBuilder.append("\"from\"").append(":").append("\"").append(0).append("\",");
			jBuilder.append("\"size\"").append(":").append("\"").append(tamanho).append("\",");
			jBuilder.append("\"query\"").append(":").append("{");
			jBuilder.append("\"match\"").append(":").append("{");
			jBuilder.append("\"nome_servidor\"").append(":").append("\"").append(nome).append("\"");
			jBuilder.append("}");
			jBuilder.append("}");
			jBuilder.append("}");
			return this.doGet(client, jBuilder, mIndex.name());
		} finally {
			this.close(client);
		}
	}

	public void indexarTodos() {
		try {
			final RestClient client = this.openConnection();
			MIndex mIndex = Folha.class.getAnnotation(MIndex.class);
			String name = mIndex.name();

			StringBuffer jBuilder = new StringBuffer();
			File folder = new File(prop.getCsv());
			int init = 0;
			int end = 0;
			for (File file : folder.listFiles()) {
				String absolutePath = file.getAbsolutePath();
				if (!absolutePath.contains(".csv")) {
					continue;
				}
//				this.executor.submit(() -> {
				System.out.println(absolutePath);

//				at.incrementAndGet();
				List<Folha> parse = this.doRead(absolutePath);
				int size = parse.size();
				int div = size / SIZE_DEFAULT;
				int rest = size % SIZE_DEFAULT;

				for (int cont = 0; cont <= div; cont++) {
					init = cont * SIZE_DEFAULT;
					if (cont == div && rest > 0) {
						end = size;
					} else if (cont < div) {
						end = init + SIZE_DEFAULT;
					} else {
						break;
					}

//					System.out.println(init +" - " +end);
					List<Folha> synchronizedList = Collections.synchronizedList(parse.subList(init, end));

//					this.executor.submit(() -> {
						jBuilder.append(synchronizedList.stream()
								.map(folha -> BulkBuilder.init(name).addId(UUID.randomUUID().toString())
										.addComando("index").addJson(this.prepareJson(folha)).build())
								.collect(Collectors.joining()));
						try {
							this.doBulk(client, jBuilder);
						} catch (IOException e) {
							e.printStackTrace();
						}
//					});
//					Thread.sleep(5000);
					jBuilder.setLength(0);

//					at.decrementAndGet();
//					System.out.println("Decrementou -> " + at.get());
				}

//				});
			}

//			System.out.println("while");
//			while(at.get() > 0 || jBuffer.length() == 0) {
//				System.out.println("At -> "+at.get());
//				Thread.sleep(10000);
//			}
			System.out.println(at.get());

			this.close(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void prepareMap(Map<String, Object> map, Folha folha) throws IllegalAccessException {
		for (Field field : folha.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(MField.class)) {
				field.setAccessible(true);
				MField mField = field.getAnnotation(MField.class);
				if (Stream.of("double", "float").anyMatch(p -> mField.type().equals(p)))
					map.put(mField.name(), Double.valueOf(field.get(folha).toString().replaceAll(",", ".")));
				else
					map.put(mField.name(), field.get(folha));
			}
		}

	}

	private String prepareJson(Folha folha) {
		try {
			StringBuilder jsonB = new StringBuilder("{");
			Field[] declaredFields = folha.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				if (field.isAnnotationPresent(MField.class)) {
					field.setAccessible(true);
					MField mField = field.getAnnotation(MField.class);
					jsonB.append("\"").append(mField.name()).append("\":\"");
					Object obj = field.get(folha);
					if (Stream.of("double", "float").anyMatch(p -> mField.type().equals(p))) {
						jsonB.append(Double.valueOf(obj.toString().replaceAll(",", ".")));
					} else {
						jsonB.append(obj);
					}
					jsonB.append("\"");

					jsonB.append(",");
				}
			}
			int size = jsonB.length();
			jsonB.replace(size - 1, size, "}");

			return jsonB.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		throw new IllegalAccessError();
	}

	public static void main(String[] args) throws IOException {
		new FolhaService().indexar("201807");
	}
}
