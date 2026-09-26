package com.hbm.dbs;

/**
 * The fork's own version identity. RefStrings.VERSION stays upstream's value (CI rewrites it and
 * every JameH2 merge changes it), so the fork is identified by a separate revision on top of it:
 * build.gradle reads REVISION from this file and appends "_DBS<revision>" to the jar and mcmod.info
 * version, and HTTPHandler's update check compares REVISION with this file on the fork's GitHub branch.
 *
 * Bump REVISION when a build is meant to supersede older DBS jars (the login message then tells an
 * older jar that a newer revision exists). Keep the declaration on one line: build.gradle and the
 * update check both parse it with a regex.
 */
public class DBSVersion {

	public static final String REVISION = "1";

	public static final String REPOSITORY = "mindbound/Hbm-s-Nuclear-Tech-DBS";
	public static final String BRANCH = "space-travel-twopointfive";

	/** Raw URL of this file on the fork's main branch, read by HTTPHandler.loadVersion(). */
	public static final String REMOTE_SOURCE = "https://raw.githubusercontent.com/" + REPOSITORY + "/" + BRANCH + "/src/main/java/com/hbm/dbs/DBSVersion.java";
	/** Where the login message sends players when a newer revision exists. */
	public static final String PROJECT_URL = "https://github.com/" + REPOSITORY;
}
