package coms.obir;

import java.awt.image.BufferedImage;
import java.util.List;

import com.stromberglabs.jopensurf.*;

public class SURFHelpers {

	private static int mOctaves = 4;
	private static int mInitSample = 2;
	private static float mHessianThreshold = (float) 0.0001;
	private static float mBalanceValue = (float) 0.3;

	public SURFHelpers() {

	}

	public List<SURFInterestPoint> getFreeOrientedInterestPoints(
			BufferedImage img) {
		Surf imgSurf = new Surf(img, mBalanceValue, mHessianThreshold, mOctaves);
		return imgSurf.getFreeOrientedInterestPoints();
	}
}
