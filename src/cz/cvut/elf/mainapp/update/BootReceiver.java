package cz.cvut.elf.mainapp.update;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cz.cvut.elf.mainapp.ElfConstants;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        	if (ElfConstants.DEBUG) {
        		Log.d("Boot", "boot broadcast received");
        	}
        	
        	CheckUpdateService.schedule(context);
        }
    }
}