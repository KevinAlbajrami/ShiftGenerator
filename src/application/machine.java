package application;

public class machine {
	int id;
	int inv;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public int getInv() {
		return inv;
	}
	public void setInv(int inv) {
		this.inv = inv;
	}
	String name;
	int volume;
	public machine(int id, String name, int volume,int inv) {
		this.id=id;
		this.name=name;
		this.volume=volume;
		this.inv=inv;
	}
	
}
