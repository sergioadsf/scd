package br.com.conectasol.scd.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.text.TextPosition;

import com.google.common.collect.Range;

public class RangeUtil {
	
	private RangeUtil() {
	}

	public static Map<Integer, List<TextPosition>> build(List<TextPosition> list) {
		
		Map<Integer, List<TextPosition>> mapLines = new HashMap<>();
		List<Range<Integer>> retVal = new ArrayList<>();
		
		for (TextPosition textPosition : list) {
			Range<Integer> range = getRangeY(textPosition);	
			if(retVal.isEmpty()) {
				retVal.add(range);
				mapLines.put(range.lowerEndpoint(), addNewList(textPosition));
			} else {
				Range<Integer> lastRange = retVal.get(retVal.size() - 1);
				if (lastRange.isConnected(range)) {
					Range<Integer> newLastRange = lastRange.span(range);
					retVal.set(retVal.size() - 1, newLastRange);
					Integer lowerEndpointLast = newLastRange.lowerEndpoint();
					
					addMap(mapLines, textPosition, lowerEndpointLast);
				} else {
					Integer lowerEndpoint = range.lowerEndpoint();
					retVal.add(range);
					
					addMap(mapLines, textPosition, lowerEndpoint);
				}
			}
		}
		
		return mapLines.entrySet().stream()
				.sorted((c1, c2) -> c1.getKey()
				.compareTo(c2.getKey()))
		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
				LinkedHashMap::new));
	}

	private static void addMap(Map<Integer, List<TextPosition>> mapLines, TextPosition textPosition,
			Integer lowerEndpoint) {
		List<TextPosition> listTP;
		if(mapLines.containsKey(lowerEndpoint )) {
			listTP = mapLines.get(lowerEndpoint);
			insertSorted(textPosition, listTP);
		} else {
			listTP = addNewList(textPosition);
		}
		mapLines.put(lowerEndpoint, listTP);
	}

	private static void insertSorted(TextPosition textPosition, List<TextPosition> listTP) {
//		int size = listTP.size();
//		boolean isAdicionou = false;
//		for (int i = 1; i < size; i++) {
//			TextPosition tpPrev = listTP.get(i - 1);
//			TextPosition tp = listTP.get(i);
//			if((textPosition.getX() > tpPrev.getX() && textPosition.getX() < tp.getX()) || i + 1 > size) {
//				listTP.add(i, textPosition);
//				isAdicionou = true;
//				break;
//			}
//			if(textPosition.getX() < tpPrev.getX()){
//				listTP.add(i - 1, textPosition);
//				isAdicionou = true;
//				break;
//			}
//		}
//		if(!isAdicionou)
		listTP.add(textPosition);
		
		listTP.sort((tp1, tp2) -> tp1.getX() > tp2.getX() ? 1 : -1);
	}

	private static List<TextPosition> addNewList(TextPosition textPosition) {
		List<TextPosition> listTP = new ArrayList<>();
		listTP.add(textPosition);
		return listTP;
	}

	public static Range<Integer> getRangeY(TextPosition textPosition) {
		return Range.closed((int )textPosition.getY(), (int) (textPosition.getY() + textPosition.getHeight()));
	}

	public static Range<Integer> getRangeX(TextPosition textPosition) {
		return Range.closed((int )textPosition.getX(), (int) (textPosition.getX() + textPosition.getWidth()));
	}

}
