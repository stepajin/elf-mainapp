package cz.cvut.elf.mainapp.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragment;

import cz.cvut.elf.mainapp.R;

public class CreateNewButtonFragment extends SherlockFragment {

	ImageButton button;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_create_new_button, container, false);
		button = (ImageButton) v.findViewById(R.id.add_new_user_image_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginActivity activity = (LoginActivity)getActivity();
				if (activity == null)
					return;
				activity.startNewUserActivity();
			}
		});

		return v;
	}
	

}
