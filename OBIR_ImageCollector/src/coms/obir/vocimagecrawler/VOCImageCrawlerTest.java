package coms.obir.vocimagecrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import coms.obir.Helpers;

public class VOCImageCrawlerTest {
	private String[] keywords;
	private Map<String, double[]> featureSpace;
	private Map<String, ImageAnnotation> imageDescription;

	public VOCImageCrawlerTest() {

		VOCImageCrawler c = new VOCImageCrawler();

		// get crawled results
		keywords = c.getKeywords();

		// get image annotations
		imageDescription = c.getImageDescription();

		// get feature space
		featureSpace = c.getFeatureSpace();

	}

	public void run_test(String[] password) {
		// test feature space
		test(password, keywords, featureSpace);

		// checking
		check();
	}

	private void test(String[] p, String[] k, Map<String, double[]> fs) {
		double[] q = new double[k.length];
		double maxima = 1.0;
		double ep = 0.0001;

		for (int i = 0; i < k.length; ++i) {
			int j;
			for (j = 0; j < p.length; ++j) {
				if (k[i].equals(p[j])) {
					break;
				}
			}
			if (j == p.length) {
				q[i] = 0.0;
			} else {
				q[i] = maxima;
			}
		}

		double maxS = 0;
		double maxR = 0;
		double maxN = 0;
		String correct = "";
		double[] c_v = null;
		for (Map.Entry<String, double[]> entry : fs.entrySet()) {
			double[] v = entry.getValue();
			double R = dist(v, q);

			if (R != 0) {
				double N = 0.0;
				double iN = 0.0;
				double count = 0;

				for (int i = 0; i < v.length; ++i) {
					if ((v[i] > 0) && (!isPassword(p, k[i]))) {

						double[] u = new double[k.length];
						for (int j = 0; j < u.length; ++j) {
							u[j] = 0.0;
						}
						u[i] = maxima;
						double delta = Math.abs(R - dist(v, u));

						// if (delta < 0.2) {
						iN += delta;
						++count;
						// }
					}
				}
				if ((Math.abs(iN) < ep) || (Math.abs(R) < ep)) {
					N = 0;
				} else {
					N = ((count - 1) / count - iN / count);
				}

				double S = R * N;

				System.out.format("%s score: %1.2f * %1.2f = %1.5f\n",
						entry.getKey(), R, N, S);
				System.out.println("\t vector: " + Helpers.array2string(v));
				System.out.println("\t iN=" + iN + "  count=" + count);
				if (maxS < S) {
					maxS = S;
					maxR = R;
					maxN = N;
					correct = entry.getKey();
					c_v = v;
				}
			}
		}

		System.out.println("Correct image: " + correct + " with score " + maxS);
		System.out.println("\t" + Helpers.array2string(c_v));
		System.out.println("\t" + maxR + "  " + maxN);
	}

	private boolean isPassword(String[] p, String s) {
		for (int i = 0; i < p.length; ++i) {
			if (p[i].equals(s)) {
				return true;
			}
		}
		return false;
	}

	private double dist(double[] u, double[] v) {
		double d = 0;
		for (int i = 0; i < u.length; ++i) {
			d += (u[i] * v[i]);
		}
		return d;
	}

	// checking image information
	private void check() {
		String input = "";
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));

		while (!input.equals("no")) {
			try {

				System.out.print("image: ");
				input = bufferRead.readLine();

				ImageAnnotation ann = imageDescription.get(input);
				if (ann != null) {
					for (ObjectDescriptor ob : ann.getObjects()) {
						System.out
								.format("\t%s imgarea=%5.3f obarea=%5.3f  ratio=%5.3f weight=%5.3f\n\n",
										ob.getName(), ann.getArea(),
										ob.getArea(), ob.getRatio(),
										ob.getWeight());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
