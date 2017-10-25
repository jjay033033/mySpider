/**
 * 
 */
package calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author guozy
 * @date 2016-12-26
 * 
 */
public class Calculator {

	public static void main(String[] args) throws Exception {
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("a2a", (double) 3);
		// 9*-1-(-1+(3.2-4)) * 2
//		System.out.println(cal("-(23*-2a2a(-5+-a2a)a2a)", map));
//		System.out.println(-(23 * -2 * 3 * (-5 + -3) * 3));
		System.out.println(cal("1+2*3+9*8*7-8", null));
	}

	private static Stack<Character> oprStk = new Stack<Character>();
	private static StringBuffer numTemp = new StringBuffer();
	private static StringBuffer epTemp = new StringBuffer();
	@SuppressWarnings("rawtypes")
	private static List list = new ArrayList();

	@SuppressWarnings("rawtypes")
	public static double cal(String str, Map map) throws Exception {
		List list = getExp(str, map);
		System.out.println(list);
		Stack<Double> numStk = new Stack<Double>();
		double a;
		double b;
		for (Object o : list) {
			if (o instanceof Double) {
				numStk.push((Double) o);
			} else if (o instanceof Character) {
				System.out.println(o);
				System.out.println(numStk);
				b = numStk.pop();
				a = numStk.pop();
				try {
					double calTemp = cal(a, b, (Character) o);
					numStk.push(calTemp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				continue;
		}
		return numStk.pop();
	}

	@SuppressWarnings("rawtypes")
	public static List getExp(String str, Map map) throws Exception {

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ('+' == c) {
				doPlus(str, c, i, map);
			} else if ('-' == c) {
				doMinus(str, c, i, map);
			} else if ('*' == c) {
				doMultiply(str, c, i, map);
			} else if ('/' == c) {
				doDivide(str, c, i, map);
			} else if (Character.isDigit(c) || '.' == c) {
				doDigit(str, c, i, map);
			} else if ('(' == c) {
				doLeftBracket(str, c, i, map);
			} else if (')' == c) {
				doRightBracket(str, c, i, map);
			} else if (Character.isLetter(c)) {
				doLetter(str, c, i, map);
			}
		}
		// 将剩余的数字和运算符都add到list里
		addNumOrEpToList(map);
		while (!oprStk.isEmpty()) {
			addOprToList();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static void addNumToList() {
		if (numTemp.length() > 0) {
			list.add(Double.valueOf(numTemp.toString()));
			numTemp.setLength(0);
		}
	}

	@SuppressWarnings("unchecked")
	public static void addOprToList() {
		list.add(oprStk.pop());
	}

	public static void addMulAndDivToList() {
		if (!oprStk.isEmpty() && "*/".indexOf(oprStk.peek()) > -1) {
			addOprToList();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void addEpToList(Map map) throws Exception {
		if (epTemp.length() > 0) {
			Object o = map.get(epTemp.toString());
			if (o == null) {
				throw new Exception("Map doesn't contains some Object!");
			}
			double num = (Double) o;
			list.add(num);
			epTemp.setLength(0);
		}
	}

	public static void addNumOrEpToList(Map map) throws Exception {
		addNumToList();
		addEpToList(map);
	}

	public static void dealPlusOrMinus(char c, Map map) throws Exception {
		addNumOrEpToList(map);
		while (!oprStk.isEmpty() && oprStk.peek() != '(') {
			addOprToList();
		}
		oprStk.push(c);
	}

	public static boolean isSign(String str, int i) {
		return i == 0 || ("+-*/(".indexOf(str.charAt(i - 1)) > -1);
	}

	public static void dealMulOrDiv(char c, Map map) throws Exception {
		addNumOrEpToList(map);
		addMulAndDivToList();
		oprStk.push(c);
	}

	@SuppressWarnings("rawtypes")
	public static void doPlus(String str, char c, int i, Map map)
			throws Exception {
		// 处理正数
		if (isSign(str, i)) {
			return;
		} else {
			dealPlusOrMinus(c, map);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void doMinus(String str, char c, int i, Map map)
			throws Exception {
		// 处理负数（*-1）
		if (isSign(str, i)) {
			list.add((double) -1);
			oprStk.push('*');
		} else {
			dealPlusOrMinus(c, map);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void doMultiply(String str, char c, int i, Map map)
			throws Exception {
		dealMulOrDiv(c, map);
	}

	@SuppressWarnings("rawtypes")
	public static void doDivide(String str, char c, int i, Map map)
			throws Exception {
		dealMulOrDiv(c, map);
	}

	@SuppressWarnings("rawtypes")
	public static void doLeftBracket(String str, char c, int i, Map map)
			throws Exception {
		if (i > 0 && Character.isLetterOrDigit(str.charAt(i - 1))) {
			addNumOrEpToList(map);
			addMulAndDivToList();
			oprStk.push('*');
		}
		oprStk.push(c);
	}

	@SuppressWarnings("rawtypes")
	public static void doRightBracket(String str, char c, int i, Map map)
			throws Exception {
		addNumOrEpToList(map);
		while (oprStk.peek() != '(') {
			addOprToList();
		}
		// 把相应'('符号pop出来
		oprStk.pop();
		if ((i + 1) < str.length()
				&& Character.isLetterOrDigit(str.charAt(i + 1))) {
			oprStk.push('*');
		}
	}

	@SuppressWarnings("rawtypes")
	public static void doDigit(String str, char c, int i, Map map) {
		if (epTemp.length() > 0) {
			epTemp.append(c);
		} else {
			numTemp.append(c);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void doLetter(String str, char c, int i, Map map) {
		if (i > 0 && Character.isDigit(str.charAt(i - 1))
				&& epTemp.length() == 0) {
			addNumToList();
			oprStk.push('*');
		}
		epTemp.append(c);
	}

	public static double cal(double a, double b, char opr) throws Exception {
		double result = 0;
		switch (opr) {
		case '+':
			result = a + b;
			break;
		case '-':
			result = a - b;
			break;
		case '*':
			result = a * b;
			break;
		case '/':
			result = a / b;
			break;
		default:
			throw new Exception("No such operator!");
		}
		return result;
	}

}
