package cz.cvut.elf.mainapp.planetselection;

import java.io.Serializable;

public class Task implements Serializable {

	String name;
	int id;

	public Task(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	static class Result {
		int time;
		int accuracy;
		int difficulty;
	}
}
