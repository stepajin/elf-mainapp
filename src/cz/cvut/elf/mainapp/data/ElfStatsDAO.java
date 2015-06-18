package cz.cvut.elf.mainapp.data;

import java.util.ArrayList;
import java.util.List;

import cz.cvut.elf.mainapp.planetselection.Task;

public class ElfStatsDAO {
	static ElfStatsDAO INSTANCE;

	List<Task> tasks = new ArrayList<Task>();

	public static ElfStatsDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ElfStatsDAO();
		}

		return INSTANCE;
	}

	private ElfStatsDAO() {
	}
	
	public void loadTasks() {
		
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
}
