package ph.edu.msuiit.circuitlens;

import org.junit.Test;
import org.opencv.core.Mat;

import ph.edu.msuiit.circuitlens.render.OverlayImageTransformationMapper;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by vercillius on 4/27/2016.
 */
public class OverlayImageTransformationMapperTest {

    final Mat trackingImg = null;
    OverlayImage overlayImg = new AnimatedOverlayImage();
    OverlayImageTransformationMapper mapper;

    @Test
    public void testOverlay(){
        mapper = new OverlayImageTransformationMapper();
        assertTrue(true);
    }


}
