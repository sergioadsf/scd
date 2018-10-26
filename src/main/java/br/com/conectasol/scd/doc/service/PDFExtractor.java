package br.com.conectasol.scd.doc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;

import com.google.common.collect.Range;

import br.com.conectasol.scd.util.CloseUtil;
import br.com.conectasol.scd.util.RangeUtil;

public class PDFExtractor {

	private PDDocument document;

	public PDFExtractor(String path) throws IOException {
		this(new File(path));
	}

	public PDFExtractor(File file) throws IOException {
		System.out.println(file.getName());
		this.document = PDDocument.load(file);
	}

	public Map<Integer, Map<Integer, List<TextPosition>>> readDocument() throws IOException {
		return readPages();
	}

	public void save(String out) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);

			fos.write(getDocumentText().getBytes());
		} finally {
			CloseUtil.close(fos);
		}
	}

	public String getDocumentText() throws IOException {
		Map<Integer, Map<Integer, List<TextPosition>>> mapDocument = readDocument();
		StringBuilder sb = new StringBuilder();
		boolean isInicioLinha = true;
		for (Integer keyPage : mapDocument.keySet()) {
			Map<Integer, List<TextPosition>> mapPage = mapDocument.get(keyPage);
			for (Integer keyLine : mapPage.keySet()) {
				List<TextPosition> listTP = mapPage.get(keyLine);
				for (int i = 0; i < listTP.size(); i++) {
					TextPosition tp = listTP.get(i);
					Range<Integer> range = RangeUtil.getRangeX(tp);
					if (isInicioLinha) {
						sb.append(tp.toString());
						isInicioLinha = false;
					} else {
						TextPosition tpPrev = listTP.get(i - 1);
						Range<Integer> rangePrev = RangeUtil.getRangeX(tpPrev);
						if (!rangePrev.isConnected(range)) {
							sb.append(" ");
						}
						sb.append(tp.toString());
					}
				}
				isInicioLinha = true;
//				System.out.println(sb.toString());
				sb.append("\n");
			}
//			System.out.println();
		}
		return sb.toString();
	}

	private Map<Integer, Map<Integer, List<TextPosition>>> readPages() throws IOException {
		Map<Integer, List<TextPosition>> mapTP = new HashMap<>();
		for (int pageId = 0; pageId < document.getNumberOfPages(); pageId++) {
			PDFStripperLocal pdfStripper = new PDFStripperLocal(document, pageId);
			mapTP.put(pageId, pdfStripper.extract());
		}

		return readLines(mapTP);
	}

	private Map<Integer, Map<Integer, List<TextPosition>>> readLines(Map<Integer, List<TextPosition>> mapTP) {
		Map<Integer, Map<Integer, List<TextPosition>>> mapLines = new HashMap<>();
		for (Integer key : mapTP.keySet()) {
			mapLines.put(key, RangeUtil.build(mapTP.get(key)));
		}

		return mapLines;
	}
}
