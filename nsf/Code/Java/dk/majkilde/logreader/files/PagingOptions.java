package dk.majkilde.logreader.files;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

/** Shared settings for text-log paging; compatible with Java 8 / JSF 1.x. */
public final class PagingOptions {
	public static final String DEFAULT_SIZE = "1000";
	private static final List<String> SIZES = Collections.unmodifiableList(
			Arrays.asList("250", DEFAULT_SIZE, "5000", "10000"));
	private static final int BOTTOM_NAVIGATION_LINES = 100;

	private PagingOptions() { }

	public static int getDefaultSize() { return Integer.parseInt(DEFAULT_SIZE); }
	public static boolean isAllowed(int size) { return SIZES.contains(Integer.toString(size)); }
	public static boolean showBottomNavigation(int lines) { return lines >= BOTTOM_NAVIGATION_LINES; }
	public static List<SelectItem> getSelectItems() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (String size : SIZES) items.add(new SelectItem(size, size));
		return items;
	}
}
