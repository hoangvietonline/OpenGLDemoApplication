package jp.co.cyberagent.android.gpuimage.filter;

import android.opengl.GLES20;

public class GPUImageGlitch01 extends GPUImageFilter {
    public static final String HUE_FRAGMENT_SHADER = "" +
            "\n" +
            "\n" +
            "precision highp float;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "//necessary\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform float  intensity;\n" +
            "\n" +
            "//end necessary\n" +
            "\n" +
            "// Code taken from https://www.shadertoy.com/view/XsfGzj and modified to work in this context as an example\n" +
            "// of how quickly and easily you can get up and running with shadercam\n" +
            "\n" +
            "// passed in via our SuperAwesomeRenderer.java\n" +
            "uniform float\tiTime;\n" +
            "\n" +
            "// play around with xy for different sized effect, or pass in via GLES20.glUniform3f();\n" +
            "uniform vec3    iResolution;\n" +
            "\n" +
            "#define VHS 1\n" +
            "\n" +
            "void main ()\n" +
            "{\n" +
            "    vec2 uv = textureCoordinate.xy;\n" +
            "    float amount = sin(iTime) * 0.1;\n" +
            "\n" +
            "    amount *= 0.3;\n" +
            "    float split = 1. - fract(iTime / 2.);\n" +
            "    float scanOffset = 0.01;\n" +
            "    vec2 uv1 = vec2(uv.x + amount, uv.y);\n" +
            "    vec2 uv2 = vec2(uv.x, uv.y + amount);\n" +
            "    if (uv.y > split) {\n" +
            "        uv.x += scanOffset;\n" +
            "        uv1.x += scanOffset;\n" +
            "        uv2.x += scanOffset;\n" +
            "    }\n" +
            "\n" +
            "    float r = texture2D(inputImageTexture, uv1).r;\n" +
            "    float g = texture2D(inputImageTexture, uv).g;\n" +
            "    float b = texture2D(inputImageTexture, uv2).b;\n" +
            "\n" +
            "    gl_FragColor = vec4(r, g, b, 1.);\n" +
            "\n" +
            "     vec4 texori = texture2D(inputImageTexture, textureCoordinate.xy);\n" +
            "    gl_FragColor = vec4(texori.rgb*(1.0-intensity) + gl_FragColor.rgb*intensity,1.);\n" +
            "}";

    private float glitch01;
    private int hueLocation;

    public GPUImageGlitch01() {
        this(2f);
    }

    public GPUImageGlitch01(final float glitch01) {
        super(NO_FILTER_VERTEX_SHADER, HUE_FRAGMENT_SHADER);
        this.glitch01 = glitch01;
    }

    @Override
    public void onInit() {
        super.onInit();
        hueLocation = GLES20.glGetUniformLocation(getProgram(), "intensity");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setGlitch01(glitch01);
    }

    public void setGlitch01(final float glitch01) {
        this.glitch01 = glitch01;
        setFloat(hueLocation, glitch01);
    }
}
