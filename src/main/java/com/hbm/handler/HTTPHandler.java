package com.hbm.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.hbm.dbs.DBSVersion;
import com.hbm.main.MainRegistry;

public class HTTPHandler {

	public static List<String> capsule = new ArrayList();
	public static List<String> tipOfTheDay = new ArrayList();
	public static boolean newVersion = false;
	public static String versionNumber = "";

	public static void loadStats() {

		Thread versionChecker = new Thread("NTM Version Checker") {

			@Override
			public void run() {
				// DBS fix B-008: each download is independent, so a failed version check no longer skips the tips
				try { loadVersion(); } catch(IOException e) { MainRegistry.logger.warn("Version checker failed!"); }
				try { loadSoyuz(); } catch(IOException e) { MainRegistry.logger.warn("Soyuz hologram text download failed!"); }
				try { loadTips(); } catch(IOException e) { MainRegistry.logger.warn("Tip of the day download failed!"); }
				try { loadSpaceTips(); } catch(IOException e) { MainRegistry.logger.warn("Space tip download failed!"); }
			}

		};

		versionChecker.start();
	}

	private static void loadVersion() throws IOException {

		// DBS fix B-008: compare the fork's own revision with the fork's branch, not JameH2's RefStrings.VERSION
		URL github = new URL(DBSVersion.REMOTE_SOURCE);
		BufferedReader in = new BufferedReader(new InputStreamReader(github.openStream()));

		MainRegistry.logger.info("Searching for new versions...");
		String line;

		while((line = in.readLine()) != null) {

			if(line.contains("String REVISION")) {

				int begin = line.indexOf('"');
				int end = line.lastIndexOf('"');

				String sub = line.substring(begin + 1, end);

				newVersion = !DBSVersion.REVISION.equals(sub);
				versionNumber = sub;
				MainRegistry.logger.info("Found version " + sub);
				break;
			}
		}

		MainRegistry.logger.info("Version checker ended.");
		in.close();
	}

	private static void loadSoyuz() throws IOException {

		URL github = new URL("https://gist.githubusercontent.com/HbmMods/a1cad71d00b6915945a43961d0037a43/raw/soyuz_holo");
		BufferedReader in = new BufferedReader(new InputStreamReader(github.openStream()));

		String line;
		while((line = in.readLine()) != null) capsule.add(line);
		in.close();
	}

	private static void loadTips() throws IOException {

		URL github = new URL("https://gist.githubusercontent.com/HbmMods/a03c66ba160184e12f43de826b30c096/raw/tip_of_the_day");
		BufferedReader in = new BufferedReader(new InputStreamReader(github.openStream()));

		String line;
		while((line = in.readLine()) != null) tipOfTheDay.add(line);
		in.close();
	}

	private static void loadSpaceTips() throws IOException {

		URL github = new URL("https://gist.githubusercontent.com/MellowArpeggiation/f9424a8773ed8530000437dfcef50d3e/raw/tip_of_the_space");
		BufferedReader in = new BufferedReader(new InputStreamReader(github.openStream()));

		String line;
		while((line = in.readLine()) != null) tipOfTheDay.add(line);
		in.close();
	}
}
