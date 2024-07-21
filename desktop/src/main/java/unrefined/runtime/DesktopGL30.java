package unrefined.runtime;

import com.jogamp.opengl.GL3bc;
import com.jogamp.opengl.awt.AWTGLAutoDrawable;
import unrefined.math.FastMath;
import unrefined.media.opengl.GL30;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class DesktopGL30 extends GL30 {

    private final AWTGLAutoDrawable drawable;
    private final ByteBuffer stringBuffer = ByteBuffer.allocate(
            FastMath.max(
            GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, GL_ACTIVE_UNIFORM_MAX_LENGTH,
                    FastMath.max(GL_INFO_LOG_LENGTH, GL_SHADER_SOURCE_LENGTH, Math.max(GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH, GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH))));
    private final IntBuffer stringLengthBuffer = IntBuffer.allocate(1);
    private final ByteBuffer booleanBuffer = ByteBuffer.allocate(1);

    public DesktopGL30(AWTGLAutoDrawable drawable) {
        this.drawable = Objects.requireNonNull(drawable);
    }

    public AWTGLAutoDrawable getGLAutoDrawable() {
        return drawable;
    }
    
    private GL3bc gl() {
        return drawable.getGL().getGL3bc();
    }

    @Override
    public void glActiveTexture(int texture) {
        gl().glActiveTexture(texture);
    }

    @Override
    public void glAttachShader(int program, int shader) {
        gl().glAttachShader(program, shader);
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name) {
        gl().glBindAttribLocation(program, index, name);
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        gl().glBindBuffer(target, buffer);
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        gl().glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        gl().glBindRenderbuffer(target, renderbuffer);
    }

    @Override
    public void glBindTexture(int target, int texture) {
        gl().glBindTexture(target, texture);
    }

    @Override
    public void glBlendColor(float red, float green, float blue, float alpha) {
        gl().glBlendColor(red, green, blue, alpha);
    }

    @Override
    public void glBlendEquation(int mode) {
        gl().glBlendEquation(mode);
    }

    @Override
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        gl().glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        gl().glBlendFunc(sfactor, dfactor);
    }

    @Override
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        gl().glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override
    public void glBufferData(int target, int size, Buffer data, int usage) {
        gl().glBufferData(target, size, data, usage);
    }

    @Override
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        gl().glBufferSubData(target, offset, size, data);
    }

    @Override
    public int glCheckFramebufferStatus(int target) {
        return gl().glCheckFramebufferStatus(target);
    }

    @Override
    public void glClear(int mask) {
        gl().glClear(mask);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        gl().glClearColor(red, green, blue, alpha);
    }

    @Override
    public void glClearDepthf(float depth) {
        gl().glClearDepthf(depth);
    }

    @Override
    public void glClearStencil(int s) {
        gl().glClearStencil(s);
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        gl().glColorMask(red, green, blue, alpha);
    }

    @Override
    public void glCompileShader(int shader) {
        gl().glCompileShader(shader);
    }

    @Override
    public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
        gl().glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
    }

    @Override
    public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
        gl().glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
    }

    @Override
    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        gl().glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }

    @Override
    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        gl().glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    @Override
    public int glCreateProgram() {
        return gl().glCreateProgram();
    }

    @Override
    public int glCreateShader(int type) {
        return gl().glCreateShader(type);
    }

    @Override
    public void glCullFace(int mode) {
        gl().glCullFace(mode);
    }

    @Override
    public void glDeleteBuffers(int n, int[] buffers, int offset) {
        gl().glDeleteBuffers(n, buffers, offset);
    }

    @Override
    public void glDeleteBuffers(int n, IntBuffer buffers) {
        gl().glDeleteBuffers(n, buffers);
    }

    @Override
    public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
        gl().glDeleteFramebuffers(n, framebuffers, offset);
    }

    @Override
    public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        gl().glDeleteFramebuffers(n, framebuffers);
    }

    @Override
    public void glDeleteProgram(int program) {
        gl().glDeleteProgram(program);
    }

    @Override
    public void glDeleteRenderbuffers(int n, int[] renderbuffers, int offset) {
        gl().glDeleteRenderbuffers(n, renderbuffers, offset);
    }

    @Override
    public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        gl().glDeleteRenderbuffers(n, renderbuffers);
    }

    @Override
    public void glDeleteShader(int shader) {
        gl().glDeleteShader(shader);
    }

    @Override
    public void glDeleteTextures(int n, int[] textures, int offset) {
        gl().glDeleteTextures(n, textures, offset);
    }

    @Override
    public void glDeleteTextures(int n, IntBuffer textures) {
        gl().glDeleteTextures(n, textures);
    }

    @Override
    public void glDepthFunc(int func) {
        gl().glDepthFunc(func);
    }

    @Override
    public void glDepthMask(boolean flag) {
        gl().glDepthMask(flag);
    }

    @Override
    public void glDepthRangef(float zNear, float zFar) {
        gl().glDepthRangef(zNear, zFar);
    }

    @Override
    public void glDetachShader(int program, int shader) {
        gl().glDetachShader(program, shader);
    }

    @Override
    public void glDisable(int cap) {
        gl().glDisable(cap);
    }

    @Override
    public void glDisableVertexAttribArray(int index) {
        gl().glDisableVertexAttribArray(index);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        gl().glDrawArrays(mode, first, count);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, int offset) {
        gl().glDrawElements(mode, count, type, offset);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        gl().glDrawElements(mode, count, type, indices);
    }

    @Override
    public void glEnable(int cap) {
        gl().glEnable(cap);
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        gl().glEnableVertexAttribArray(index);
    }

    @Override
    public void glFinish() {
        gl().glFinish();
    }

    @Override
    public void glFlush() {
        gl().glFlush();
    }

    @Override
    public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        gl().glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        gl().glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override
    public void glFrontFace(int mode) {
        gl().glFrontFace(mode);
    }

    @Override
    public void glGenBuffers(int n, int[] buffers, int offset) {
        gl().glGenBuffers(n, buffers, offset);
    }

    @Override
    public void glGenBuffers(int n, IntBuffer buffers) {
        gl().glGenBuffers(n, buffers);
    }

    @Override
    public void glGenerateMipmap(int target) {
        gl().glGenerateMipmap(target);
    }

    @Override
    public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
        gl().glGenFramebuffers(n, framebuffers, offset);
    }

    @Override
    public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        gl().glGenFramebuffers(n, framebuffers);
    }

    @Override
    public void glGenRenderbuffers(int n, int[] renderbuffers, int offset) {
        gl().glGenRenderbuffers(n, renderbuffers, offset);
    }

    @Override
    public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        gl().glGenRenderbuffers(n, renderbuffers);
    }

    @Override
    public void glGenTextures(int n, int[] textures, int offset) {
        gl().glGenTextures(n, textures, offset);
    }

    @Override
    public void glGenTextures(int n, IntBuffer textures) {
        gl().glGenTextures(n, textures);
    }

    @Override
    public void glGetActiveAttrib(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        gl().glGetActiveAttrib(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
    }

    @Override
    public String glGetActiveAttrib(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        synchronized (stringBuffer) {
            gl().glGetActiveAttrib(program, index, GL_ACTIVE_UNIFORM_MAX_LENGTH, stringLengthBuffer.array(), 0, size, sizeOffset, type, typeOffset, stringBuffer.array(), 0);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public String glGetActiveAttrib(int program, int index, IntBuffer size, IntBuffer type) {
        synchronized (stringBuffer) {
            gl().glGetActiveAttrib(program, index, GL_ACTIVE_UNIFORM_MAX_LENGTH, stringLengthBuffer, size, type, stringBuffer);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public void glGetActiveUniform(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        gl().glGetActiveUniform(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
    }

    @Override
    public String glGetActiveUniform(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        synchronized (stringBuffer) {
            gl().glGetActiveUniform(program, index, GL_ACTIVE_UNIFORM_MAX_LENGTH, stringLengthBuffer.array(), 0, size, sizeOffset, type, typeOffset, stringBuffer.array(), 0);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }


    @Override
    public String glGetActiveUniform(int program, int index, IntBuffer size, IntBuffer type) {
        synchronized (stringBuffer) {
            gl().glGetActiveUniform(program, index, GL_ACTIVE_UNIFORM_MAX_LENGTH, stringLengthBuffer, size, type, stringBuffer);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public void glGetAttachedShaders(int program, int maxcount, int[] count, int countOffset, int[] shaders, int shadersOffset) {

    }

    @Override
    public void glGetAttachedShaders(int program, int maxcount, IntBuffer count, IntBuffer shaders) {

    }

    @Override
    public int glGetAttribLocation(int program, String name) {
        return gl().glGetAttribLocation(program, name);
    }

    @Override
    public void glGetBooleanv(int pname, boolean[] params, int offset) {
        synchronized (booleanBuffer) {
            gl().glGetBooleanv(pname, booleanBuffer.array(), 0);
            params[offset] = booleanBuffer.get(0) != 0;
        }
    }

    @Override
    public void glGetBooleanv(int pname, IntBuffer params) {
        synchronized (booleanBuffer) {
            gl().glGetBooleanv(pname, booleanBuffer);
            params.put(booleanBuffer.get(0));
        }
    }

    @Override
    public void glGetBufferParameteriv(int target, int pname, int[] params, int offset) {
        gl().glGetBufferParameteriv(target, pname, params, offset);
    }

    @Override
    public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        gl().glGetBufferParameteriv(target, pname, params);
    }

    @Override
    public int glGetError() {
        return gl().glGetError();
    }

    @Override
    public void glGetFloatv(int pname, float[] params, int offset) {
        gl().glGetFloatv(pname, params, offset);
    }

    @Override
    public void glGetFloatv(int pname, FloatBuffer params) {
        gl().glGetFloatv(pname, params);
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, int[] params, int offset) {
        gl().glGetFramebufferAttachmentParameteriv(target, attachment, pname, params, offset);
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        gl().glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
    }

    @Override
    public void glGetIntegerv(int pname, int[] params, int offset) {
        gl().glGetIntegerv(pname, params, offset);
    }

    @Override
    public void glGetIntegerv(int pname, IntBuffer params) {
        gl().glGetIntegerv(pname, params);
    }

    @Override
    public void glGetProgramiv(int program, int pname, int[] params, int offset) {
        gl().glGetProgramiv(program, pname, params, offset);
    }

    @Override
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        gl().glGetProgramiv(program, pname, params);
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        synchronized (stringBuffer) {
            gl().glGetProgramInfoLog(program, GL_INFO_LOG_LENGTH, stringLengthBuffer, stringBuffer);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public void glGetRenderbufferParameteriv(int target, int pname, int[] params, int offset) {
        gl().glGetRenderbufferParameteriv(target, pname, params, offset);
    }

    @Override
    public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        gl().glGetRenderbufferParameteriv(target, pname, params);
    }

    @Override
    public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
        gl().glGetShaderiv(shader, pname, params, offset);
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        gl().glGetShaderiv(shader, pname, params);
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        synchronized (stringBuffer) {
            gl().glGetShaderInfoLog(shader, GL_INFO_LOG_LENGTH, stringLengthBuffer, stringBuffer);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, int[] range, int rangeOffset, int[] precision, int precisionOffset) {
        gl().glGetShaderPrecisionFormat(shadertype, precisiontype, range, rangeOffset, precision, precisionOffset);
    }

    @Override
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {

    }

    @Override
    public void glGetShaderSource(int shader, int bufsize, int[] length, int lengthOffset, byte[] source, int sourceOffset) {

    }

    @Override
    public void glGetShaderSource(int shader, int bufsize, IntBuffer length, byte source) {

    }

    @Override
    public String glGetShaderSource(int shader) {
        synchronized (stringBuffer) {
            gl().glGetShaderSource(shader, GL_SHADER_SOURCE_LENGTH, stringLengthBuffer, stringBuffer);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public String glGetString(int name) {
        return gl().glGetString(name);
    }

    @Override
    public void glGetTexParameterfv(int target, int pname, float[] params, int offset) {
        gl().glGetTexParameterfv(target, pname, params, offset);
    }

    @Override
    public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        gl().glGetTexParameterfv(target, pname, params);
    }

    @Override
    public void glGetTexParameteriv(int target, int pname, int[] params, int offset) {
        gl().glGetTexParameteriv(target, pname, params, offset);
    }

    @Override
    public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        gl().glGetTexParameteriv(target, pname, params);
    }

    @Override
    public void glGetUniformfv(int program, int location, float[] params, int offset) {
        gl().glGetUniformfv(program, location, params, offset);
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params) {
        gl().glGetUniformfv(program, location, params);
    }

    @Override
    public void glGetUniformiv(int program, int location, int[] params, int offset) {
        gl().glGetUniformiv(program, location, params, offset);
    }

    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params) {
        gl().glGetUniformiv(program, location, params);
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        return gl().glGetUniformLocation(program, name);
    }

    @Override
    public void glGetVertexAttribfv(int index, int pname, float[] params, int offset) {
        gl().glGetVertexAttribfv(index, pname, params, offset);
    }

    @Override
    public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        gl().glGetVertexAttribfv(index, pname, params);
    }

    @Override
    public void glGetVertexAttribiv(int index, int pname, int[] params, int offset) {
        gl().glGetVertexAttribiv(index, pname, params, offset);
    }

    @Override
    public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        gl().glGetVertexAttribiv(index, pname, params);
    }

    @Override
    public void glHint(int target, int mode) {
        gl().glHint(target, mode);
    }

    @Override
    public boolean glIsBuffer(int buffer) {
        return gl().glIsBuffer(buffer);
    }

    @Override
    public boolean glIsEnabled(int cap) {
        return gl().glIsEnabled(cap);
    }

    @Override
    public boolean glIsFramebuffer(int framebuffer) {
        return gl().glIsFramebuffer(framebuffer);
    }

    @Override
    public boolean glIsProgram(int program) {
        return gl().glIsProgram(program);
    }

    @Override
    public boolean glIsRenderbuffer(int renderbuffer) {
        return gl().glIsRenderbuffer(renderbuffer);
    }

    @Override
    public boolean glIsShader(int shader) {
        return gl().glIsShader(shader);
    }

    @Override
    public boolean glIsTexture(int texture) {
        return gl().glIsTexture(texture);
    }

    @Override
    public void glLineWidth(float width) {
        gl().glLineWidth(width);
    }

    @Override
    public void glLinkProgram(int program) {
        gl().glLinkProgram(program);
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        gl().glPixelStorei(pname, param);
    }

    @Override
    public void glPolygonOffset(float factor, float units) {
        gl().glPolygonOffset(factor, units);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        gl().glReadPixels(x, y, width, height, format, type, pixels);
    }

    @Override
    public void glReleaseShaderCompiler() {
        gl().glReleaseShaderCompiler();
    }

    @Override
    public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        gl().glRenderbufferStorage(target, internalformat, width, height);
    }

    @Override
    public void glSampleCoverage(float value, boolean invert) {
        gl().glSampleCoverage(value, invert);
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        gl().glScissor(x, y, width, height);
    }

    @Override
    public void glShaderBinary(int n, int[] shaders, int offset, int binaryformat, Buffer binary, int length) {
        gl().glShaderBinary(n, shaders, offset, binaryformat, binary, length);
    }

    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        gl().glShaderBinary(n, shaders, binaryformat, binary, length);
    }

    @Override
    public void glShaderSource(int shader, String... string) {
        int[] lengths = new int[string.length];
        for (int i = 0; i < string.length; i++) {
            lengths[i] = string[i].length();
        }
        gl().glShaderSource(shader, string.length, string, lengths, 0);
    }

    @Override
    public void glStencilFunc(int func, int ref, int mask) {
        gl().glStencilFunc(func, ref, mask);
    }

    @Override
    public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        gl().glStencilFuncSeparate(face, func, ref, mask);
    }

    @Override
    public void glStencilMask(int mask) {
        gl().glStencilMask(mask);
    }

    @Override
    public void glStencilMaskSeparate(int face, int mask) {
        gl().glStencilMaskSeparate(face, mask);
    }

    @Override
    public void glStencilOp(int fail, int zfail, int zpass) {
        gl().glStencilOp(fail, zfail, zpass);
    }

    @Override
    public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        gl().glStencilOpSeparate(face, fail, zfail, zpass);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        gl().glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
    }

    @Override
    public void glTexParameterf(int target, int pname, float param) {
        gl().glTexParameterf(target, pname, param);
    }

    @Override
    public void glTexParameterfv(int target, int pname, float[] params, int offset) {
        gl().glTexParameterfv(target, pname, params, offset);
    }

    @Override
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        gl().glTexParameterfv(target, pname, params);
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        gl().glTexParameteri(target, pname, param);
    }

    @Override
    public void glTexParameteriv(int target, int pname, int[] params, int offset) {
        gl().glTexParameteriv(target, pname, params, offset);
    }

    @Override
    public void glTexParameteriv(int target, int pname, IntBuffer params) {
        gl().glTexParameteriv(target, pname, params);
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
        gl().glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
    }

    @Override
    public void glUniform1f(int location, float x) {
        gl().glUniform1f(location, x);
    }

    @Override
    public void glUniform1fv(int location, int count, float[] v, int offset) {
        gl().glUniform1fv(location, count, v, offset);
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer v) {
        gl().glUniform1fv(location, count, v);
    }

    @Override
    public void glUniform1i(int location, int x) {
        gl().glUniform1i(location, x);
    }

    @Override
    public void glUniform1iv(int location, int count, int[] v, int offset) {
        gl().glUniform1iv(location, count, v, offset);
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer v) {
        gl().glUniform1iv(location, count, v);
    }

    @Override
    public void glUniform2f(int location, float x, float y) {
        gl().glUniform2f(location, x, y);
    }

    @Override
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        gl().glUniform2fv(location, count, v, offset);
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer v) {
        gl().glUniform2fv(location, count, v);
    }

    @Override
    public void glUniform2i(int location, int x, int y) {
        gl().glUniform2i(location, x, y);
    }

    @Override
    public void glUniform2iv(int location, int count, int[] v, int offset) {
        gl().glUniform2iv(location, count, v, offset);
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer v) {
        gl().glUniform2iv(location, count, v);
    }

    @Override
    public void glUniform3f(int location, float x, float y, float z) {
        gl().glUniform3f(location, x, y, z);
    }

    @Override
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        gl().glUniform3fv(location, count, v, offset);
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer v) {
        gl().glUniform3fv(location, count, v);
    }

    @Override
    public void glUniform3i(int location, int x, int y, int z) {
        gl().glUniform3i(location, x, y, z);
    }

    @Override
    public void glUniform3iv(int location, int count, int[] v, int offset) {
        gl().glUniform3iv(location, count, v, offset);
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer v) {
        gl().glUniform3iv(location, count, v);
    }

    @Override
    public void glUniform4f(int location, float x, float y, float z, float w) {
        gl().glUniform4f(location, x, y, z, w);
    }

    @Override
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        gl().glUniform4fv(location, count, v, offset);
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer v) {
        gl().glUniform4fv(location, count, v);
    }

    @Override
    public void glUniform4i(int location, int x, int y, int z, int w) {
        gl().glUniform4i(location, x, y, z, w);
    }

    @Override
    public void glUniform4iv(int location, int count, int[] v, int offset) {
        gl().glUniform4iv(location, count, v, offset);
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer v) {
        gl().glUniform4iv(location, count, v);
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix2fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix2fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix3fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix3fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix4fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix4fv(location, count, transpose, value);
    }

    @Override
    public void glUseProgram(int program) {
        gl().glUseProgram(program);
    }

    @Override
    public void glValidateProgram(int program) {
        gl().glValidateProgram(program);
    }

    @Override
    public void glVertexAttrib1f(int indx, float x) {
        gl().glVertexAttrib1f(indx, x);
    }

    @Override
    public void glVertexAttrib1fv(int indx, float[] values, int offset) {
        gl().glVertexAttrib1fv(indx, values, offset);
    }

    @Override
    public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        gl().glVertexAttrib1fv(indx, values);
    }

    @Override
    public void glVertexAttrib2f(int indx, float x, float y) {
        gl().glVertexAttrib2f(indx, x, y);
    }

    @Override
    public void glVertexAttrib2fv(int indx, float[] values, int offset) {
        gl().glVertexAttrib2fv(indx, values, offset);
    }

    @Override
    public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        gl().glVertexAttrib2fv(indx, values);
    }

    @Override
    public void glVertexAttrib3f(int indx, float x, float y, float z) {
        gl().glVertexAttrib3f(indx, x, y, z);
    }

    @Override
    public void glVertexAttrib3fv(int indx, float[] values, int offset) {
        gl().glVertexAttrib3fv(indx, values, offset);
    }

    @Override
    public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        gl().glVertexAttrib3fv(indx, values);
    }

    @Override
    public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        gl().glVertexAttrib4f(indx, x, y, z, w);
    }

    @Override
    public void glVertexAttrib4fv(int indx, float[] values, int offset) {
        gl().glVertexAttrib4fv(indx, values, offset);
    }

    @Override
    public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        gl().glVertexAttrib4fv(indx, values);
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset) {
        gl().glVertexAttribPointer(indx, size, type, normalized, stride, offset);
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        gl().glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        gl().glViewport(x, y, width, height);
    }

    @Override
    public void glReadBuffer(int mode) {
        gl().glReadBuffer(mode);
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        gl().glDrawRangeElements(mode, start, end, count, type, indices);
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        gl().glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        gl().glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {
        gl().glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        gl().glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        gl().glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override
    public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        gl().glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override
    public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, Buffer data) {
        gl().glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, data);
    }

    @Override
    public void glCompressedTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int imageSize, int offset) {
        gl().glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, offset);
    }

    @Override
    public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, Buffer data) {
        gl().glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, data);
    }

    @Override
    public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize, int offset) {
        gl().glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, imageSize, offset);
    }

    @Override
    public void glGenQueries(int n, int[] ids, int offset) {
        gl().glGenQueries(n, ids, offset);
    }

    @Override
    public void glGenQueries(int n, IntBuffer ids) {
        gl().glGenQueries(n, ids);
    }

    @Override
    public void glDeleteQueries(int n, int[] ids, int offset) {
        gl().glDeleteQueries(n, ids, offset);
    }

    @Override
    public void glDeleteQueries(int n, IntBuffer ids) {
        gl().glDeleteQueries(n, ids);
    }

    @Override
    public boolean glIsQuery(int id) {
        return gl().glIsQuery(id);
    }

    @Override
    public void glBeginQuery(int target, int id) {
        gl().glBeginQuery(target, id);
    }

    @Override
    public void glEndQuery(int target) {
        gl().glEndQuery(target);
    }

    @Override
    public void glGetQueryiv(int target, int pname, int[] params, int offset) {
        gl().glGetQueryiv(target, pname, params, offset);
    }

    @Override
    public void glGetQueryiv(int target, int pname, IntBuffer params) {
        gl().glGetQueryiv(target, pname, params);
    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, int[] params, int offset) {
        gl().glGetQueryObjectuiv(id, pname, params, offset);
    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {
        gl().glGetQueryObjectuiv(id, pname, params);
    }

    @Override
    public boolean glUnmapBuffer(int target) {
        return gl().glUnmapBuffer(target);
    }

    @Override
    public Buffer glGetBufferPointerv(int target, int pname) {
        return gl().getBufferStorage(gl().getBoundBuffer(target)).getMappedBuffer();
    }

    @Override
    public void glDrawBuffers(int n, int[] bufs, int offset) {
        gl().glDrawBuffers(n, bufs, offset);
    }

    @Override
    public void glDrawBuffers(int n, IntBuffer bufs) {
        gl().glDrawBuffers(n, bufs);
    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix2x3fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix2x3fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix3x2fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix3x2fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix2x4fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix2x4fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix4x2fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix4x2fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix3x4fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix3x4fv(location, count, transpose, value);
    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, float[] value, int offset) {
        gl().glUniformMatrix4x3fv(location, count, transpose, value, offset);
    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        gl().glUniformMatrix4x3fv(location, count, transpose, value);
    }

    @Override
    public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        gl().glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        gl().glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override
    public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        gl().glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    @Override
    public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        return gl().glMapBufferRange(target, offset, length, access);
    }

    @Override
    public void glFlushMappedBufferRange(int target, int offset, int length) {
        gl().glFlushMappedBufferRange(target, offset, length);
    }

    @Override
    public void glBindVertexArray(int array) {
        gl().glBindVertexArray(array);
    }

    @Override
    public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        gl().glDeleteVertexArrays(n, arrays, offset);
    }

    @Override
    public void glDeleteVertexArrays(int n, IntBuffer arrays) {
        gl().glDeleteVertexArrays(n, arrays);
    }

    @Override
    public void glGenVertexArrays(int n, int[] arrays, int offset) {
        gl().glGenVertexArrays(n, arrays, offset);
    }

    @Override
    public void glGenVertexArrays(int n, IntBuffer arrays) {
        gl().glGenVertexArrays(n, arrays);
    }

    @Override
    public boolean glIsVertexArray(int array) {
        return gl().glIsVertexArray(array);
    }

    @Override
    public void glGetIntegeri_v(int target, int index, int[] data, int offset) {
        gl().glGetIntegeri_v(target, index, data, offset);
    }

    @Override
    public void glGetIntegeri_v(int target, int index, IntBuffer data) {
        gl().glGetIntegeri_v(target, index, data);
    }

    @Override
    public void glBeginTransformFeedback(int primitiveMode) {
        gl().glBeginTransformFeedback(primitiveMode);
    }

    @Override
    public void glEndTransformFeedback() {
        gl().glEndTransformFeedback();
    }

    @Override
    public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        gl().glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {
        gl().glBindBufferBase(target, index, buffer);
    }

    @Override
    public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        gl().glTransformFeedbackVaryings(program, varyings.length, varyings, bufferMode);
    }

    @Override
    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, int[] length, int lengthOffset, int[] size, int sizeOffset, int[] type, int typeOffset, byte[] name, int nameOffset) {
        gl().glGetTransformFeedbackVarying(program, index, bufsize, length, lengthOffset, size, sizeOffset, type, typeOffset, name, nameOffset);
    }

    @Override
    public void glGetTransformFeedbackVarying(int program, int index, int bufsize, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
        gl().glGetTransformFeedbackVarying(program, index, bufsize, length, size, type, name);
    }

    @Override
    public String glGetTransformFeedbackVarying(int program, int index, int[] size, int sizeOffset, int[] type, int typeOffset) {
        synchronized (stringBuffer) {
            gl().glGetTransformFeedbackVarying(program, index, GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH, stringLengthBuffer.array(), 0, size, sizeOffset, type, typeOffset, stringBuffer.array(), 0);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public String glGetTransformFeedbackVarying(int program, int index, IntBuffer size, IntBuffer type) {
        synchronized (stringBuffer) {
            gl().glGetTransformFeedbackVarying(program, index, GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH, stringLengthBuffer, size, type, stringBuffer);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, Buffer pointer) {
        gl().glVertexAttribIPointer(index, size, type, stride, pointer);
    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        gl().glVertexAttribIPointer(index, size, type, stride, offset);
    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, int[] params, int offset) {
        gl().glGetVertexAttribIiv(index, pname, params, offset);
    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {
        gl().glGetVertexAttribIiv(index, pname, params);
    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, int[] params, int offset) {
        gl().glGetVertexAttribIuiv(index, pname, params, offset);
    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {
        gl().glGetVertexAttribIuiv(index, pname, params);
    }

    @Override
    public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        gl().glVertexAttribI4i(index, x, y, z, w);
    }

    @Override
    public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        gl().glVertexAttribI4ui(index, x, y, z, w);
    }

    @Override
    public void glVertexAttribI4iv(int index, int[] v, int offset) {
        gl().glVertexAttribI4iv(index, v, offset);
    }

    @Override
    public void glVertexAttribI4iv(int index, IntBuffer v) {
        gl().glVertexAttribI4iv(index, v);
    }

    @Override
    public void glVertexAttribI4uiv(int index, int[] v, int offset) {
        gl().glVertexAttribI4uiv(index, v, offset);
    }

    @Override
    public void glVertexAttribI4uiv(int index, IntBuffer v) {
        gl().glVertexAttribI4uiv(index, v);
    }

    @Override
    public void glGetUniformuiv(int program, int location, int[] params, int offset) {
        gl().glGetUniformuiv(program, location, params, offset);
    }

    @Override
    public void glGetUniformuiv(int program, int location, IntBuffer params) {
        gl().glGetUniformuiv(program, location, params);
    }

    @Override
    public int glGetFragDataLocation(int program, String name) {
        return gl().glGetFragDataLocation(program, name);
    }

    @Override
    public void glUniform1ui(int location, int v0) {
        gl().glUniform1ui(location, v0);
    }

    @Override
    public void glUniform2ui(int location, int v0, int v1) {
        gl().glUniform2ui(location, v0, v1);
    }

    @Override
    public void glUniform3ui(int location, int v0, int v1, int v2) {
        gl().glUniform3ui(location, v0, v1, v2);
    }

    @Override
    public void glUniform4ui(int location, int v0, int v1, int v2, int v3) {
        gl().glUniform4ui(location, v0, v1, v2, v3);
    }

    @Override
    public void glUniform1uiv(int location, int count, int[] value, int offset) {
        gl().glUniform1uiv(location, count, value, offset);
    }

    @Override
    public void glUniform1uiv(int location, int count, IntBuffer value) {
        gl().glUniform1uiv(location, count, value);
    }

    @Override
    public void glUniform2uiv(int location, int count, int[] value, int offset) {
        gl().glUniform2uiv(location, count, value, offset);
    }

    @Override
    public void glUniform2uiv(int location, int count, IntBuffer value) {
        gl().glUniform2uiv(location, count, value);
    }

    @Override
    public void glUniform3uiv(int location, int count, int[] value, int offset) {
        gl().glUniform3uiv(location, count, value, offset);
    }

    @Override
    public void glUniform3uiv(int location, int count, IntBuffer value) {
        gl().glUniform3uiv(location, count, value);
    }

    @Override
    public void glUniform4uiv(int location, int count, int[] value, int offset) {
        gl().glUniform4uiv(location, count, value, offset);
    }

    @Override
    public void glUniform4uiv(int location, int count, IntBuffer value) {
        gl().glUniform4uiv(location, count, value);
    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, int[] value, int offset) {
        gl().glClearBufferiv(buffer, drawbuffer, value, offset);
    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        gl().glClearBufferiv(buffer, drawbuffer, value);
    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, int[] value, int offset) {
        gl().glClearBufferuiv(buffer, drawbuffer, value, offset);
    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        gl().glClearBufferuiv(buffer, drawbuffer, value);
    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, float[] value, int offset) {
        gl().glClearBufferfv(buffer, drawbuffer, value, offset);
    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        gl().glClearBufferfv(buffer, drawbuffer, value);
    }

    @Override
    public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        gl().glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override
    public String glGetStringi(int name, int index) {
        return gl().glGetStringi(name, index);
    }

    @Override
    public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        gl().glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, int[] uniformIndices, int uniformIndicesOffset) {
        gl().glGetUniformIndices(program, uniformNames.length, uniformNames, uniformIndices, uniformIndicesOffset);
    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        gl().glGetUniformIndices(program, uniformNames.length, uniformNames, uniformIndices);
    }

    @Override
    public void glGetActiveUniformsiv(int program, int uniformCount, int[] uniformIndices, int uniformIndicesOffset, int pname, int[] params, int paramsOffset) {
        gl().glGetActiveUniformsiv(program, uniformCount, uniformIndices, uniformIndicesOffset, pname, params, paramsOffset);
    }

    @Override
    public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        gl().glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
    }

    @Override
    public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return gl().glGetUniformBlockIndex(program, uniformBlockName);
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params, int offset) {
        gl().glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params, offset);
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        gl().glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize, int[] length, int lengthOffset, byte[] uniformBlockName, int uniformBlockNameOffset) {
        gl().glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize, length, lengthOffset, uniformBlockName, uniformBlockNameOffset);
    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        gl().glGetActiveUniformBlockName(program, uniformBlockIndex, uniformBlockName.limit(), (IntBuffer) length, (ByteBuffer) uniformBlockName);
    }

    @Override
    public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        synchronized (stringBuffer) {
            gl().glGetActiveUniformBlockName(program, uniformBlockIndex, GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH, stringLengthBuffer.array(), 0, stringBuffer.array(), 0);
            return new String(stringBuffer.array(), 0, stringLengthBuffer.get(0), StandardCharsets.UTF_8);
        }
    }

    @Override
    public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        gl().glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override
    public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        gl().glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, Buffer indices, int instanceCount) {
        gl().glDrawElementsInstanced(mode, count, type, indices, instanceCount);
    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        gl().glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
    }

    @Override
    public long glFenceSync(int condition, int flags) {
        return gl().glFenceSync(condition, flags);
    }

    @Override
    public boolean glIsSync(long sync) {
        return gl().glIsSync(sync);
    }

    @Override
    public void glDeleteSync(long sync) {
        gl().glDeleteSync(sync);
    }

    @Override
    public int glClientWaitSync(long sync, int flags, long timeout) {
        return gl().glClientWaitSync(sync, flags, timeout);
    }

    @Override
    public void glWaitSync(long sync, int flags, long timeout) {
        gl().glWaitSync(sync, flags, timeout);
    }

    @Override
    public void glGetInteger64v(int pname, long[] params, int offset) {
        gl().glGetInteger64v(pname, params, offset);
    }

    @Override
    public void glGetInteger64v(int pname, LongBuffer params) {
        gl().glGetInteger64v(pname, params);
    }

    @Override
    public void glGetSynciv(long sync, int pname, int bufSize, int[] length, int lengthOffset, int[] values, int valuesOffset) {
        gl().glGetSynciv(sync, pname, bufSize, length, lengthOffset, values, valuesOffset);
    }

    @Override
    public void glGetSynciv(long sync, int pname, int bufSize, IntBuffer length, IntBuffer values) {
        gl().glGetSynciv(sync, pname, bufSize, length, values);
    }

    @Override
    public void glGetInteger64i_v(int target, int index, long[] data, int offset) {
        gl().glGetInteger64i_v(target, index, data, offset);
    }

    @Override
    public void glGetInteger64i_v(int target, int index, LongBuffer data) {
        gl().glGetInteger64i_v(target, index, data);
    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, long[] params, int offset) {
        gl().glGetBufferParameteri64v(target, pname, params, offset);
    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {
        gl().glGetBufferParameteri64v(target, pname, params);
    }

    @Override
    public void glGenSamplers(int count, int[] samplers, int offset) {
        gl().glGenSamplers(count, samplers, offset);
    }

    @Override
    public void glGenSamplers(int count, IntBuffer samplers) {
        gl().glGenSamplers(count, samplers);
    }

    @Override
    public void glDeleteSamplers(int count, int[] samplers, int offset) {
        gl().glDeleteSamplers(count, samplers, offset);
    }

    @Override
    public void glDeleteSamplers(int count, IntBuffer samplers) {
        gl().glDeleteSamplers(count, samplers);
    }

    @Override
    public boolean glIsSampler(int sampler) {
        return gl().glIsSampler(sampler);
    }

    @Override
    public void glBindSampler(int unit, int sampler) {
        gl().glBindSampler(unit, sampler);
    }

    @Override
    public void glSamplerParameteri(int sampler, int pname, int param) {
        gl().glSamplerParameteri(sampler, pname, param);
    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, int[] param, int offset) {
        gl().glSamplerParameteriv(sampler, pname, param, offset);
    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {
        gl().glSamplerParameteriv(sampler, pname, param);
    }

    @Override
    public void glSamplerParameterf(int sampler, int pname, float param) {
        gl().glSamplerParameterf(sampler, pname, param);
    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, float[] param, int offset) {
        gl().glSamplerParameterfv(sampler, pname, param, offset);
    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {
        gl().glSamplerParameterfv(sampler, pname, param);
    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, int[] params, int offset) {
        gl().glGetSamplerParameteriv(sampler, pname, params, offset);
    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        gl().glGetSamplerParameteriv(sampler, pname, params);
    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, float[] params, int offset) {
        gl().glGetSamplerParameterfv(sampler, pname, params, offset);
    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        gl().glGetSamplerParameterfv(sampler, pname, params);
    }

    @Override
    public void glVertexAttribDivisor(int index, int divisor) {
        gl().glVertexAttribDivisor(index, divisor);
    }

    @Override
    public void glBindTransformFeedback(int target, int id) {
        gl().glBindTransformFeedback(target, id);
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        gl().glDeleteTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {
        gl().glDeleteTransformFeedbacks(n, ids);
    }

    @Override
    public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        gl().glGenTransformFeedbacks(n, ids, offset);
    }

    @Override
    public void glGenTransformFeedbacks(int n, IntBuffer ids) {
        gl().glGenTransformFeedbacks(n, ids);
    }

    @Override
    public boolean glIsTransformFeedback(int id) {
        return gl().glIsTransformFeedback(id);
    }

    @Override
    public void glPauseTransformFeedback() {
        gl().glPauseTransformFeedback();
    }

    @Override
    public void glResumeTransformFeedback() {
        gl().glResumeTransformFeedback();
    }

    @Override
    public void glGetProgramBinary(int program, int bufSize, int[] length, int lengthOffset, int[] binaryFormat, int binaryFormatOffset, Buffer binary) {
        gl().glGetProgramBinary(program, bufSize, length, lengthOffset, binaryFormat, binaryFormatOffset, binary);
    }

    @Override
    public void glGetProgramBinary(int program, int bufSize, IntBuffer length, IntBuffer binaryFormat, Buffer binary) {
        gl().glGetProgramBinary(program, bufSize, length, binaryFormat, binary);
    }

    @Override
    public void glProgramBinary(int program, int binaryFormat, Buffer binary, int length) {
        gl().glProgramBinary(program, binaryFormat, binary, length);
    }

    @Override
    public void glProgramParameteri(int program, int pname, int value) {
        gl().glProgramParameteri(program, pname, value);
    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, int[] attachments, int offset) {
        gl().glInvalidateFramebuffer(target, numAttachments, attachments, offset);
    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        gl().glInvalidateFramebuffer(target, numAttachments, attachments);
    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, int[] attachments, int offset, int x, int y, int width, int height) {
        gl().glInvalidateSubFramebuffer(target, numAttachments, attachments, offset, x, y, width, height);
    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {
        gl().glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
    }

    @Override
    public void glTexStorage2D(int target, int levels, int internalformat, int width, int height) {
        gl().glTexStorage2D(target, levels, internalformat, width, height);
    }

    @Override
    public void glTexStorage3D(int target, int levels, int internalformat, int width, int height, int depth) {
        gl().glTexStorage3D(target, levels, internalformat, width, height, depth);
    }

    @Override
    public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, int[] params, int offset) {
        gl().glGetInternalformativ(target, internalformat, pname, bufSize, params, offset);
    }

    @Override
    public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize, IntBuffer params) {
        gl().glGetInternalformativ(target, internalformat, pname, bufSize, params);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, int offset) {
        gl().glReadPixels(x, y, width, height, format, type, offset);
    }
    
}
