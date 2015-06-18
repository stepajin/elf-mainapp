package cz.cvut.elf.mainapp.login;

import java.io.Serializable;
import java.util.GregorianCalendar;

import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.R;

public class User implements Serializable {

	public static int images[] = { R.drawable.players_kocicka,
			R.drawable.players_skritek, R.drawable.players_owl,
			R.drawable.players_mrtile, R.drawable.players_picasso };

	public static User selectedUser = null;

	String name;
	int icon;
	int id;
	char gender;
	GregorianCalendar bornDate;

	public static int getBitmap(int i) {
		if (i < 0 || i >= images.length) {
			return -1;
		}
		return images[i];
	}

	public User(int id, String name, char gender, GregorianCalendar bornDate,
			int icon) {
		this.name = name;
		this.gender = gender;
		this.bornDate = bornDate;
		this.icon = icon;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public GregorianCalendar getBornDate() {
		return bornDate;
	}

	public int getAge() {
		GregorianCalendar date = bornDate;
		GregorianCalendar today = new GregorianCalendar();

		int age = today.get(GregorianCalendar.YEAR)
				- date.get(GregorianCalendar.YEAR) - 1;
		if (today.get(GregorianCalendar.MONTH) > date
				.get(GregorianCalendar.MONTH)
				|| (today.get(GregorianCalendar.MONTH) == date
						.get(GregorianCalendar.MONTH) && today
						.get(GregorianCalendar.DAY_OF_MONTH) >= date
						.get(GregorianCalendar.DAY_OF_MONTH))) {
			age++;
		}

		return age;
	}

	public int getIcon() {
		return icon;
	}

	public int getId() {
		return id;
	}

	public char getGender() {
		return gender;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBornDate(GregorianCalendar bornDate) {
		this.bornDate = bornDate;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setGender(char c) {
		if (c == ElfConstants.MALE || c == ElfConstants.FEMALE) {
			gender = c;
		}
	}
}
