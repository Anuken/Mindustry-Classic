package io.anuke.mindustry;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.provider.Settings.*;
import android.telephony.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.android.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.utils.*;
import io.anuke.kryonet.*;
import io.anuke.mindustry.core.*;
import io.anuke.mindustry.core.ThreadHandler.*;
import io.anuke.mindustry.net.Net;
import io.anuke.ucore.core.*;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.layout.*;
import io.anuke.ucore.util.*;

import java.lang.System;
import java.text.*;
import java.util.*;

public class AndroidLauncher extends AndroidApplication{
	boolean doubleScaleTablets = true;
	int WRITE_REQUEST_CODE = 1;
	Runnable permCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		Platform.instance = new Platform(){
			DateFormat format = SimpleDateFormat.getDateTimeInstance();

			@Override
			public boolean hasDiscord() {
				return isPackageInstalled("com.discord");
			}

			@Override
			public String format(Date date){
				return format.format(date);
			}

			@Override
			public String format(int number){
				return NumberFormat.getIntegerInstance().format(number);
			}

			@Override
			public void addDialog(TextField field, int length){
				TextFieldDialogListener.add(field, 0, length);
			}

			@Override
			public String getLocaleName(Locale locale){
				return locale.getDisplayName(locale);
			}

			@Override
			public void requestWritePerms() {
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
					if(permCallback != null){
						Gdx.app.postRunnable(permCallback);
						permCallback = null;
					}
				}else{
					ArrayList<String> perms = new ArrayList<>();
					if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
						perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
					}
					if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
						perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
					}
					requestPermissions(perms.toArray(new String[0]), WRITE_REQUEST_CODE);
				}
			}

			@Override
			public ThreadProvider getThreadProvider() {
				return new DefaultThreadImpl();
			}

			@Override
			public boolean isDebug() {
				return false;
			}

			@Override
			public byte[] getUUID() {
				try {
					String s = Secure.getString(getContext().getContentResolver(),
							Secure.ANDROID_ID);

					int len = s.length();
					byte[] data = new byte[len / 2];
					for (int i = 0; i < len; i += 2) {
						data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
								+ Character.digit(s.charAt(i + 1), 16));
					}

					if(new String(Base64Coder.encode(data)).equals("AAAAAAAAAOA=")) throw new RuntimeException("Bad UUID.");

					return data;
				}catch (Exception e){

                    String uuid = Settings.getString("uuid", "");
                    if(uuid.isEmpty()){
                        byte[] result = new byte[8];
                        new Random().nextBytes(result);
                        uuid = new String(Base64Coder.encode(result));
                        Settings.putString("uuid", uuid);
                        Settings.save();
                        return result;
                    }
                    return Base64Coder.decode(uuid);
				}
			}
		};

		if(doubleScaleTablets && isTablet(this.getContext())){
			Unit.dp.addition = 0.5f;
		}
		
		config.hideStatusBar = true;

        Net.setClientProvider(new KryoClient());
        Net.setServerProvider(new KryoServer());

        initialize(new Mindustry(){
			@Override
			public void init(){
				super.init();

				//mindustry, non-classic is installed, check for classic files.
				if(isPackageInstalled("io.anuke.mindustry")){
					if(!Settings.getBool("v40importcheck", false)){
						Settings.putBool("v40importcheck", true);
						Settings.save();

						Gdx.app.postRunnable(() -> {
							Gdx.app.postRunnable(() -> {
								Vars.ui.showConfirm("Import 3.5 Data", "A Mindustry installation has been detected.\nCheck for exported Mindustry Classic save files from 3.5 build 40?", () -> {
									permCallback = () -> {
										if(Gdx.files.external("MindustryClassic").exists()){
											Vars.ui.showConfirm("Confirm Import", "Exported save data found. Import it now and restart?\n\n[scarlet]This will clear all existing game data and exit the app.", () -> {
												FileHandle saves = Gdx.files.external("MindustryClassic").child("mindustry-saves");
												if(saves.exists()){
													Gdx.files.local("mindustry-saves").deleteDirectory();
													saves.copyTo(Gdx.files.local("test").parent());
													Log.info("copied saves");
												}

												FileHandle maps = Gdx.files.external("MindustryClassic").child("mindustry-maps");
												if(maps.exists()){
													Gdx.files.local("mindustry-maps").deleteDirectory();
													maps.copyTo(Gdx.files.local("test").parent());
													Log.info("copied maps");
												}
												Log.info(Arrays.toString(Gdx.files.local("test").parent().list()));

												System.exit(0);
											});
										}else{
											Vars.ui.showInfo("No exported data found.");
										}
									};
									Platform.instance.requestWritePerms();
								});
							});
						});
					}
				}
			}
		}, config);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
		if(requestCode == WRITE_REQUEST_CODE){
			for(int i : grantResults){
				if(i != PackageManager.PERMISSION_GRANTED) return;
			}

			if(permCallback != null){
				Gdx.app.postRunnable(permCallback);
			}
		}
	}

	private boolean isPackageInstalled(String packagename) {
	    try {
	    	getPackageManager().getPackageInfo(packagename, 0);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	private boolean isTablet(Context context) {
		TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE;
	}
}
