package coms.obir.googleimagecrawler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.Gson;
import coms.obir.CrawledImages;

public class GoogleImageCrawler {
	private static final String CSE_ID = "013532982321022545805:h6x-fwxpyce";
	private static final String GOOGLEAPI_ID = "AIzaSyDRDP8g7wevPOtuQOG7HiIjy4Eel46d-Lk";
	private static final String API_ROOT = "https://www.googleapis.com/customsearch/v1";

	private static String base_url;

	private String searchType;
	private String query_url;
	private String charset;
	private int numberOfResults;
	private int start;

	private String searchQuery;

	public GoogleImageCrawler() {
		initDefault();
	}

	public GoogleImageCrawler(String query) {
		this.searchQuery = query;

		initDefault();
	}

	public void initDefault() {
		searchType = "image";
		charset = "UTF-8";
		numberOfResults = 10;
		start = 1;

		GoogleImageCrawler.base_url = API_ROOT + "?key=" + GOOGLEAPI_ID
				+ "&cx=" + CSE_ID;
	}

	public String getSearchQuery() {
		return this.searchQuery;
	}

	public void setSearchQuery(String kw) {
		this.searchQuery = kw;
	}

	public String getSearchType() {
		return this.searchType;
	}

	public void setSearchType(String type) {
		if ((type == null) || ("image".equals(type))) {
			this.searchType = type;
		} else {
			this.searchType = "image";
		}
	}

	public String getCharset() {
		return this.charset;
	}

	public void setCharset(String cs) {
		this.charset = cs;
	}

	public int getNumberOfResults() {
		return this.numberOfResults;
	}

	public void setNumberOfResults(int num) {
		this.numberOfResults = num;
	}

	public int getStart() {
		return this.start;
	}

	public void setStart(int num) {
		this.start = num;
	}

	public CrawledImages getSearchResults() {
		return getSearchResults(this.searchQuery);
	}

	public CrawledImages getSearchResults(String query) {

		URL url = null;
		CrawlResults results = new CrawlResults();
		for (int i = 0; i < 3; ++i) {
			try {
				url = new URL(generateSearchURL());

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				Reader reader = new InputStreamReader(url.openStream(), charset);
				CrawlResults interResults = new Gson().fromJson(reader,
						CrawlResults.class);

				results.appendResults(interResults);

				conn.disconnect();
				this.start += this.numberOfResults;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("searchType=" + this.searchType);
		System.out.println("q=" + this.searchQuery);
		System.out.println("Query URL: " + url);
		System.out.println("Number of results: "
				+ results.getCrawledImages().size());

		return results.getCrawledImages();
	}

	private String generateSearchURL() {
		try {
			this.query_url = GoogleImageCrawler.base_url + "&searchType="
					+ this.searchType + "&start=" + this.start + "&num="
					+ this.numberOfResults + "&q="
					+ URLEncoder.encode(this.searchQuery, this.charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query_url;
	}
}
