package br.com.conectasol.scd;

public class Teste {

	public static void main(String[] args) {
		String r = "(([1-9]\\d{0,2}(.\\d{3})*)|(([1-9]\\d*)?\\d))(\\,\\d\\d)?$";
		System.out.println("200,00".matches(r));
		System.out.println("10.200,00".matches(r));
		System.out.println("10.000,0".matches(r));
	}
}
