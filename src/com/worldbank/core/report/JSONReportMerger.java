package com.worldbank.core.report;

import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReportMerger {
	private static String reportFileName = "cucumber.json";
	private static String reportImageExtension = "png";


	public static void main(String[] args) throws Throwable {
		File reportDirectory = new File(args[0]);
		if (reportDirectory.exists()) {
			JSONReportMerger munger = new JSONReportMerger();
			munger.mergeReports(reportDirectory);
		}
	}

	public void mergeReports(File reportDirectory) throws Throwable {
		Path targetReportPath = Paths.get(reportDirectory.toString() + File.separator + reportFileName);
		if (Files.exists(targetReportPath, LinkOption.NOFOLLOW_LINKS)) {
			FileUtils.forceDelete(targetReportPath.toFile());
		}

//		File mergedReport = null;
		Collection<File> existingReports = FileUtils.listFiles(reportDirectory, new String[] { "json" }, true);

		for (File report : existingReports) {
			// if (report.getName().equals(reportFileName)) {
			renameEmbededImages(report);
			renameFeatureIDsAndNames(report);

//			if (mergedReport == null) {
//				FileUtils.copyFileToDirectory(report, reportDirectory);
//				mergedReport = new File(reportDirectory, report.getName());
//				File oldFile = new File(mergedReport.toPath().toString());
//				mergedReport = new File(reportDirectory + "/cucumber.json");
//
//				oldFile.renameTo(mergedReport);
//			} else {
//				mergeFiles(mergedReport, report);
//			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void mergeFiles(File target, File source) throws Throwable {

		String targetReport = FileUtils.readFileToString(target);
		String sourceReport = FileUtils.readFileToString(source);

		JSONParser jp = new JSONParser();

		try {
			JSONArray parsedTargetJSON = (JSONArray) jp.parse(targetReport);
			JSONArray parsedSourceJSON = (JSONArray) jp.parse(sourceReport);

			parsedTargetJSON.addAll(parsedSourceJSON);

			Writer writer = new JSONWriter();

			parsedTargetJSON.writeJSONString(writer);

			FileUtils.writeStringToFile(target, writer.toString());
		} catch (ParseException pe) {
			pe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked"})
	public void renameFeatureIDsAndNames(File reportFile) throws Throwable {
		String reportDirName = reportFile.getParentFile().getName();
		String fileAsString = FileUtils.readFileToString(reportFile);
		JSONParser jp = new JSONParser();

		try {
			JSONArray parsedJSON = (JSONArray) jp.parse(fileAsString);

			for (Object o : parsedJSON) {
				JSONObject jo = (JSONObject) o;
				String curFeatureID = jo.get("id").toString();
				String curFeatureName = jo.get("name").toString();
				String curUriName = jo.get("uri").toString();
				String[] uriArray = curUriName.split("/");
				curUriName = uriArray[uriArray.length - 1];
				String newFeatureID = String.format("%s - %s", reportDirName, curFeatureID);
				String newFeatureName = String.format("%s - %s", reportDirName, curFeatureName);
				String newUriName = String.format("%s - %s", reportDirName, curUriName);


				jo.put("id", newFeatureID);
				jo.put("name", newFeatureName);
				jo.put("uri", newUriName);
				// added for steps location update
				JSONArray elements = ((JSONArray) jo.get("elements"));
				String name = "";
				for (int i = 0; i < elements.size(); i++) {
					JSONObject stepObj = ((JSONObject) elements.get(i));
					JSONArray steps = ((JSONArray) stepObj.get("steps"));
					if (stepObj.containsKey("before")) {
						stepObj.remove("before");
					}
					if (stepObj.containsKey("after")) {
						stepObj.remove("after");
					}
					for (int k = 0; k < steps.size(); k++) {
						name = ((JSONObject) steps.get(k)).get("name").toString();
						JSONObject matchObj = (JSONObject) ((JSONObject) steps.get(k)).get("match");
						matchObj.put("location", name);
					}

				}
			}
			Writer writer = new JSONWriter();
			parsedJSON.writeJSONString(writer);
			FileUtils.writeStringToFile(reportFile, writer.toString());
		} catch (ParseException pe) {
			pe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void renameEmbededImages(File reportFile) throws Throwable {
		File reportDirectory = reportFile.getParentFile();
		Collection<File> embeddedImages = FileUtils.listFiles(reportDirectory, new String[] { reportImageExtension },
				true);

		String fileAsString = FileUtils.readFileToString(reportFile);

		for (File image : embeddedImages) {
			String curImageName = image.getName();
			String uniqueImageName = reportDirectory.getName() + "-" + UUID.randomUUID().toString() + "."
					+ reportImageExtension;

			image.renameTo(new File(reportDirectory, uniqueImageName));
			fileAsString = fileAsString.replace(curImageName, uniqueImageName);
		}

		FileUtils.writeStringToFile(reportFile, fileAsString);
	}
}