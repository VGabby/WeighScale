package com.bach.weighscale;

public class DuLieu {
	private int id;
	private String thoigian;
	private String can;
	private String tongkhoiluong;
	private String laixe;
	private String biensoxe;
	private String diadiem;
	private String nglamchung;

	public DuLieu(int id, String thoigian, String can, String tongkhoiluong,
			String laixe, String biensoxe, String diadiem, String nglamchung) {
		super();
		this.id = id;
		this.thoigian = thoigian;
		this.can = can;
		this.tongkhoiluong = tongkhoiluong;
		this.laixe = laixe;
		this.biensoxe = biensoxe;
		this.diadiem = diadiem;
		this.nglamchung = nglamchung;
	}

	public String getDiadiem() {
		return diadiem;
	}

	public void setDiadiem(String diadiem) {
		this.diadiem = diadiem;
	}

	public String getThoigian() {
		return thoigian;
	}

	public String getCan() {
		return can;
	}

	public String getTongkhoiluong() {
		return tongkhoiluong;
	}

	public String getLaixe() {
		return laixe;
	}

	public String getBiensoxe() {
		return biensoxe;
	}

	public String getNglamchung() {
		return nglamchung;
	}

	public void setThoigian(String thoigian) {
		this.thoigian = thoigian;
	}

	public void setCan(String can) {
		this.can = can;
	}

	public void setTongkhoiluong(String tongkhoiluong) {
		this.tongkhoiluong = tongkhoiluong;
	}

	public void setLaixe(String laixe) {
		this.laixe = laixe;
	}

	public void setBiensoxe(String biensoxe) {
		this.biensoxe = biensoxe;
	}

	public void setNglamchung(String nglamchung) {
		this.nglamchung = nglamchung;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
