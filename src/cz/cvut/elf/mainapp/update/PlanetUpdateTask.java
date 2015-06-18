package cz.cvut.elf.mainapp.update;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.data.ElfPlanetsDAO;
import cz.cvut.elf.mainapp.planetselection.Planet;

public class PlanetUpdateTask extends AsyncTask<Void, Void, Void> {
	List<Planet> planets;
	List<Planet> newPlanets = new ArrayList<Planet>();;
	Context context;
	boolean isFromService;

	public PlanetUpdateTask(Context context, boolean isFromService) {
		this.context = context;
		this.isFromService = isFromService;
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (ElfConstants.DEBUG) {
			Log.d("Update", "Started");
		}

		if (!isOnline()) {
			Log.d("Elf Update", "Connection unnavaible");
			return null;
		}

		String xml = getXmlFromUrl(ElfConstants.PLANETS_XML_URL);
		if (xml == null) {
			return null;
		}
		planets = parsePlanetsFromXml(xml);
		writeNewPlanets();
		notificate();
		downloadNewPlanetBitmaps();
		saveNewBitmaps();
		checkIfPlanetIsInstalled();

		if (ElfConstants.DEBUG) {
			Log.d("Update", "finished");
		}

		return null;
	}

	private void notificate() {
		if (!isFromService) {
			return;
		}
		
		for (Planet p : newPlanets) {
			CheckUpdateService.notificate(p, context);
		}
	}

	private void writeNewPlanets() {
		ElfPlanetsDAO dao = ElfPlanetsDAO.getInstance();
		dao.loadPlanets(context);
		for (Planet p : planets) {
			if (!dao.containPlanet(p.getShortcut())) {
				newPlanets.add(p);
				dao.writePlanet(p, context);
			}
		}
	}

	private void saveNewBitmaps() {
		ElfPlanetsDAO dao = ElfPlanetsDAO.getInstance();
		for (Planet p : newPlanets) {
			if (ElfConstants.DEBUG) {
				Log.d("SAVE BITMAP", p.getShortcut());
			}

			dao.savePlanetBitmap(p.getBitmap(), p.getShortcut(), context);
		}
	}

	private void checkIfPlanetIsInstalled() {
		for (Planet p : newPlanets) {
			try {
				String pckg = ElfConstants.NAMESPACE_PREFIX + p.getShortcut();
				ApplicationInfo info = context.getPackageManager()
						.getApplicationInfo(pckg, 0);
				ElfPlanetsDAO.getInstance().updatePlanetInstalled(
						p.getShortcut(), true, context);
			} catch (PackageManager.NameNotFoundException e) {

			}
		}

	}

	private void downloadNewPlanetBitmaps() {
		if (ElfConstants.DEBUG && newPlanets.isEmpty()) {
			Log.d("After download", "No new planets");
		}

		for (Planet p : newPlanets) {
			String url = ElfConstants.SERVER_DIR_URL + p.getShortcut() + ".png";
			p.setBitmap(downloadBitmap(url));
		}
	}

	private Bitmap downloadBitmap(String url) {
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);

					if (ElfConstants.DEBUG) {
						Log.d("Download", "OK");
					}

					return bitmap;

				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private List<Planet> parsePlanetsFromXml(String xml) {
		List<Planet> planets = new ArrayList<Planet>();

		try {
			XmlPullParser xpp = Xml.newPullParser();
			StringReader reader = new StringReader(xml);
			ByteArrayInputStream is = new ByteArrayInputStream(
					xml.getBytes(("ISO-8859-1")));
			xpp.setInput(is, null);

			int eventType = xpp.getEventType();
			String shortcutText = "";
			String czAttr = "";

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG
						&& xpp.getName().equals(ElfConstants.XML_TAG_PLANET)) {
					czAttr = xpp.getAttributeValue(null,
							ElfConstants.XML_ATTR_CZ);
				} else if (eventType == XmlPullParser.TEXT) {
					shortcutText = xpp.getText();
				} else if (eventType == XmlPullParser.END_TAG
						&& xpp.getName().equals(ElfConstants.XML_TAG_PLANET)) {

					planets.add(new Planet(shortcutText, czAttr, false));
				}

				eventType = xpp.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return planets;
	}

	private String getXmlFromUrl(String url) {
		String xml = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
