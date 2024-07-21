/*
**
** Copyright 2009, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package unrefined.media.opengl;

public abstract class GL {

    public boolean isGLES20Supported() {
        return this instanceof GL20;
    }

    public boolean isGLES30Supported() {
        return this instanceof GL30;
    }

    public GL20 getGL20() {
        return this instanceof GL20 ? (GL20) this : null;
    }

    public GL30 getGL30() {
        return this instanceof GL30 ? (GL30) this : null;
    }

    // C function void glActiveTexture ( GLenum texture )

    public abstract void glActiveTexture(
            int texture
    );

    // C function void glAttachShader ( GLuint program, GLuint shader )

    public abstract void glAttachShader(
            int program,
            int shader
    );

    // C function void glBindAttribLocation ( GLuint program, GLuint index, const char *name )

    public abstract void glBindAttribLocation(
            int program,
            int index,
            String name
    );

    // C function void glBindBuffer ( GLenum target, GLuint buffer )

    public abstract void glBindBuffer(
            int target,
            int buffer
    );

    // C function void glBindFramebuffer ( GLenum target, GLuint framebuffer )

    public abstract void glBindFramebuffer(
            int target,
            int framebuffer
    );

    // C function void glBindRenderbuffer ( GLenum target, GLuint renderbuffer )

    public abstract void glBindRenderbuffer(
            int target,
            int renderbuffer
    );

    // C function void glBindTexture ( GLenum target, GLuint texture )

    public abstract void glBindTexture(
            int target,
            int texture
    );

    // C function void glBlendColor ( GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha )

    public abstract void glBlendColor(
            float red,
            float green,
            float blue,
            float alpha
    );

    // C function void glBlendEquation ( GLenum mode )

    public abstract void glBlendEquation(
            int mode
    );

    // C function void glBlendEquationSeparate ( GLenum modeRGB, GLenum modeAlpha )

    public abstract void glBlendEquationSeparate(
            int modeRGB,
            int modeAlpha
    );

    // C function void glBlendFunc ( GLenum sfactor, GLenum dfactor )

    public abstract void glBlendFunc(
            int sfactor,
            int dfactor
    );

    // C function void glBlendFuncSeparate ( GLenum srcRGB, GLenum dstRGB, GLenum srcAlpha, GLenum dstAlpha )

    public abstract void glBlendFuncSeparate(
            int srcRGB,
            int dstRGB,
            int srcAlpha,
            int dstAlpha
    );

    // C function void glBufferData ( GLenum target, GLsizeiptr size, const GLvoid *data, GLenum usage )

    public abstract void glBufferData(
            int target,
            int size,
            java.nio.Buffer data,
            int usage
    );

    // C function void glBufferSubData ( GLenum target, GLintptr offset, GLsizeiptr size, const GLvoid *data )

    public abstract void glBufferSubData(
            int target,
            int offset,
            int size,
            java.nio.Buffer data
    );

    // C function GLenum glCheckFramebufferStatus ( GLenum target )

    public abstract int glCheckFramebufferStatus(
            int target
    );

    // C function void glClear ( GLbitfield mask )

    public abstract void glClear(
            int mask
    );

    // C function void glClearColor ( GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha )

    public abstract void glClearColor(
            float red,
            float green,
            float blue,
            float alpha
    );

    // C function void glClearDepthf ( GLclampf depth )

    public abstract void glClearDepthf(
            float depth
    );

    // C function void glClearStencil ( GLint s )

    public abstract void glClearStencil(
            int s
    );

    // C function void glColorMask ( GLboolean red, GLboolean green, GLboolean blue, GLboolean alpha )

    public abstract void glColorMask(
            boolean red,
            boolean green,
            boolean blue,
            boolean alpha
    );

    // C function void glCompileShader ( GLuint shader )

    public abstract void glCompileShader(
            int shader
    );

    // C function void glCompressedTexImage2D ( GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height, GLint border, GLsizei imageSize, const GLvoid *data )

    public abstract void glCompressedTexImage2D(
            int target,
            int level,
            int internalformat,
            int width,
            int height,
            int border,
            int imageSize,
            java.nio.Buffer data
    );

    // C function void glCompressedTexSubImage2D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLsizei width, GLsizei height, GLenum format, GLsizei imageSize, const GLvoid *data )

    public abstract void glCompressedTexSubImage2D(
            int target,
            int level,
            int xoffset,
            int yoffset,
            int width,
            int height,
            int format,
            int imageSize,
            java.nio.Buffer data
    );

    // C function void glCopyTexImage2D ( GLenum target, GLint level, GLenum internalformat, GLint x, GLint y, GLsizei width, GLsizei height, GLint border )

    public abstract void glCopyTexImage2D(
            int target,
            int level,
            int internalformat,
            int x,
            int y,
            int width,
            int height,
            int border
    );

    // C function void glCopyTexSubImage2D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint x, GLint y, GLsizei width, GLsizei height )

    public abstract void glCopyTexSubImage2D(
            int target,
            int level,
            int xoffset,
            int yoffset,
            int x,
            int y,
            int width,
            int height
    );

    // C function GLuint glCreateProgram ( void )

    public abstract int glCreateProgram(
    );

    // C function GLuint glCreateShader ( GLenum type )

    public abstract int glCreateShader(
            int type
    );

    // C function void glCullFace ( GLenum mode )

    public abstract void glCullFace(
            int mode
    );

    // C function void glDeleteBuffers ( GLsizei n, const GLuint *buffers )

    public abstract void glDeleteBuffers(
            int n,
            int[] buffers,
            int offset
    );

    // C function void glDeleteBuffers ( GLsizei n, const GLuint *buffers )

    public abstract void glDeleteBuffers(
            int n,
            java.nio.IntBuffer buffers
    );

    // C function void glDeleteFramebuffers ( GLsizei n, const GLuint *framebuffers )

    public abstract void glDeleteFramebuffers(
            int n,
            int[] framebuffers,
            int offset
    );

    // C function void glDeleteFramebuffers ( GLsizei n, const GLuint *framebuffers )

    public abstract void glDeleteFramebuffers(
            int n,
            java.nio.IntBuffer framebuffers
    );

    // C function void glDeleteProgram ( GLuint program )

    public abstract void glDeleteProgram(
            int program
    );

    // C function void glDeleteRenderbuffers ( GLsizei n, const GLuint *renderbuffers )

    public abstract void glDeleteRenderbuffers(
            int n,
            int[] renderbuffers,
            int offset
    );

    // C function void glDeleteRenderbuffers ( GLsizei n, const GLuint *renderbuffers )

    public abstract void glDeleteRenderbuffers(
            int n,
            java.nio.IntBuffer renderbuffers
    );

    // C function void glDeleteShader ( GLuint shader )

    public abstract void glDeleteShader(
            int shader
    );

    // C function void glDeleteTextures ( GLsizei n, const GLuint *textures )

    public abstract void glDeleteTextures(
            int n,
            int[] textures,
            int offset
    );

    // C function void glDeleteTextures ( GLsizei n, const GLuint *textures )

    public abstract void glDeleteTextures(
            int n,
            java.nio.IntBuffer textures
    );

    // C function void glDepthFunc ( GLenum func )

    public abstract void glDepthFunc(
            int func
    );

    // C function void glDepthMask ( GLboolean flag )

    public abstract void glDepthMask(
            boolean flag
    );

    // C function void glDepthRangef ( GLclampf zNear, GLclampf zFar )

    public abstract void glDepthRangef(
            float zNear,
            float zFar
    );

    // C function void glDetachShader ( GLuint program, GLuint shader )

    public abstract void glDetachShader(
            int program,
            int shader
    );

    // C function void glDisable ( GLenum cap )

    public abstract void glDisable(
            int cap
    );

    // C function void glDisableVertexAttribArray ( GLuint index )

    public abstract void glDisableVertexAttribArray(
            int index
    );

    // C function void glDrawArrays ( GLenum mode, GLint first, GLsizei count )

    public abstract void glDrawArrays(
            int mode,
            int first,
            int count
    );

    // C function void glDrawElements ( GLenum mode, GLsizei count, GLenum type, GLint offset )

    public abstract void glDrawElements(
            int mode,
            int count,
            int type,
            int offset
    );

    // C function void glDrawElements ( GLenum mode, GLsizei count, GLenum type, const GLvoid *indices )

    public abstract void glDrawElements(
            int mode,
            int count,
            int type,
            java.nio.Buffer indices
    );

    // C function void glEnable ( GLenum cap )

    public abstract void glEnable(
            int cap
    );

    // C function void glEnableVertexAttribArray ( GLuint index )

    public abstract void glEnableVertexAttribArray(
            int index
    );

    // C function void glFinish ( void )

    public abstract void glFinish(
    );

    // C function void glFlush ( void )

    public abstract void glFlush(
    );

    // C function void glFramebufferRenderbuffer ( GLenum target, GLenum attachment, GLenum renderbuffertarget, GLuint renderbuffer )

    public abstract void glFramebufferRenderbuffer(
            int target,
            int attachment,
            int renderbuffertarget,
            int renderbuffer
    );

    // C function void glFramebufferTexture2D ( GLenum target, GLenum attachment, GLenum textarget, GLuint texture, GLint level )

    public abstract void glFramebufferTexture2D(
            int target,
            int attachment,
            int textarget,
            int texture,
            int level
    );

    // C function void glFrontFace ( GLenum mode )

    public abstract void glFrontFace(
            int mode
    );

    // C function void glGenBuffers ( GLsizei n, GLuint *buffers )

    public abstract void glGenBuffers(
            int n,
            int[] buffers,
            int offset
    );

    // C function void glGenBuffers ( GLsizei n, GLuint *buffers )

    public abstract void glGenBuffers(
            int n,
            java.nio.IntBuffer buffers
    );

    // C function void glGenerateMipmap ( GLenum target )

    public abstract void glGenerateMipmap(
            int target
    );

    // C function void glGenFramebuffers ( GLsizei n, GLuint *framebuffers )

    public abstract void glGenFramebuffers(
            int n,
            int[] framebuffers,
            int offset
    );

    // C function void glGenFramebuffers ( GLsizei n, GLuint *framebuffers )

    public abstract void glGenFramebuffers(
            int n,
            java.nio.IntBuffer framebuffers
    );

    // C function void glGenRenderbuffers ( GLsizei n, GLuint *renderbuffers )

    public abstract void glGenRenderbuffers(
            int n,
            int[] renderbuffers,
            int offset
    );

    // C function void glGenRenderbuffers ( GLsizei n, GLuint *renderbuffers )

    public abstract void glGenRenderbuffers(
            int n,
            java.nio.IntBuffer renderbuffers
    );

    // C function void glGenTextures ( GLsizei n, GLuint *textures )

    public abstract void glGenTextures(
            int n,
            int[] textures,
            int offset
    );

    // C function void glGenTextures ( GLsizei n, GLuint *textures )

    public abstract void glGenTextures(
            int n,
            java.nio.IntBuffer textures
    );

    // C function void glGetActiveAttrib ( GLuint program, GLuint index, GLsizei bufsize, GLsizei *length, GLint *size, GLenum *type, char *name )

    public abstract void glGetActiveAttrib(
            int program,
            int index,
            int bufsize,
            int[] length,
            int lengthOffset,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset,
            byte[] name,
            int nameOffset
    );

    // C function void glGetActiveAttrib ( GLuint program, GLuint index, GLsizei bufsize, GLsizei *length, GLint *size, GLenum *type, char *name )

    public abstract String glGetActiveAttrib(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset
    );

    // C function void glGetActiveAttrib ( GLuint program, GLuint index, GLsizei bufsize, GLsizei *length, GLint *size, GLenum *type, char *name )

    public abstract String glGetActiveAttrib(
            int program,
            int index,
            java.nio.IntBuffer size,
            java.nio.IntBuffer type
    );
    // C function void glGetActiveUniform ( GLuint program, GLuint index, GLsizei bufsize, GLsizei *length, GLint *size, GLenum *type, char *name )

    public abstract void glGetActiveUniform(
            int program,
            int index,
            int bufsize,
            int[] length,
            int lengthOffset,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset,
            byte[] name,
            int nameOffset
    );

    // C function void glGetActiveUniform ( GLuint program, GLuint index, GLsizei bufsize, GLsizei *length, GLint *size, GLenum *type, char *name )

    public abstract String glGetActiveUniform(
            int program,
            int index,
            int[] size,
            int sizeOffset,
            int[] type,
            int typeOffset
    );

    // C function void glGetActiveUniform ( GLuint program, GLuint index, GLsizei bufsize, GLsizei *length, GLint *size, GLenum *type, char *name )

    public abstract String glGetActiveUniform(
            int program,
            int index,
            java.nio.IntBuffer size,
            java.nio.IntBuffer type
    );
    // C function void glGetAttachedShaders ( GLuint program, GLsizei maxcount, GLsizei *count, GLuint *shaders )

    public abstract void glGetAttachedShaders(
            int program,
            int maxcount,
            int[] count,
            int countOffset,
            int[] shaders,
            int shadersOffset
    );

    // C function void glGetAttachedShaders ( GLuint program, GLsizei maxcount, GLsizei *count, GLuint *shaders )

    public abstract void glGetAttachedShaders(
            int program,
            int maxcount,
            java.nio.IntBuffer count,
            java.nio.IntBuffer shaders
    );

    // C function GLint glGetAttribLocation ( GLuint program, const char *name )

    public abstract int glGetAttribLocation(
            int program,
            String name
    );

    // C function void glGetBooleanv ( GLenum pname, GLboolean *params )

    public abstract void glGetBooleanv(
            int pname,
            boolean[] params,
            int offset
    );

    // C function void glGetBooleanv ( GLenum pname, GLboolean *params )

    public abstract void glGetBooleanv(
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glGetBufferParameteriv ( GLenum target, GLenum pname, GLint *params )

    public abstract void glGetBufferParameteriv(
            int target,
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetBufferParameteriv ( GLenum target, GLenum pname, GLint *params )

    public abstract void glGetBufferParameteriv(
            int target,
            int pname,
            java.nio.IntBuffer params
    );

    // C function GLenum glGetError ( void )

    public abstract int glGetError(
    );

    // C function void glGetFloatv ( GLenum pname, GLfloat *params )

    public abstract void glGetFloatv(
            int pname,
            float[] params,
            int offset
    );

    // C function void glGetFloatv ( GLenum pname, GLfloat *params )

    public abstract void glGetFloatv(
            int pname,
            java.nio.FloatBuffer params
    );

    // C function void glGetFramebufferAttachmentParameteriv ( GLenum target, GLenum attachment, GLenum pname, GLint *params )

    public abstract void glGetFramebufferAttachmentParameteriv(
            int target,
            int attachment,
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetFramebufferAttachmentParameteriv ( GLenum target, GLenum attachment, GLenum pname, GLint *params )

    public abstract void glGetFramebufferAttachmentParameteriv(
            int target,
            int attachment,
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glGetIntegerv ( GLenum pname, GLint *params )

    public abstract void glGetIntegerv(
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetIntegerv ( GLenum pname, GLint *params )

    public abstract void glGetIntegerv(
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glGetProgramiv ( GLuint program, GLenum pname, GLint *params )

    public abstract void glGetProgramiv(
            int program,
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetProgramiv ( GLuint program, GLenum pname, GLint *params )

    public abstract void glGetProgramiv(
            int program,
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glGetProgramInfoLog( GLuint program, GLsizei maxLength, GLsizei * length,
    //     GLchar * infoLog);

    public abstract String glGetProgramInfoLog(
            int program
    );
    // C function void glGetRenderbufferParameteriv ( GLenum target, GLenum pname, GLint *params )

    public abstract void glGetRenderbufferParameteriv(
            int target,
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetRenderbufferParameteriv ( GLenum target, GLenum pname, GLint *params )

    public abstract void glGetRenderbufferParameteriv(
            int target,
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glGetShaderiv ( GLuint shader, GLenum pname, GLint *params )

    public abstract void glGetShaderiv(
            int shader,
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetShaderiv ( GLuint shader, GLenum pname, GLint *params )

    public abstract void glGetShaderiv(
            int shader,
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glGetShaderInfoLog( GLuint shader, GLsizei maxLength, GLsizei * length,
    //     GLchar * infoLog);

    public abstract String glGetShaderInfoLog(
            int shader
    );
    // C function void glGetShaderPrecisionFormat ( GLenum shadertype, GLenum precisiontype, GLint *range, GLint *precision )

    public abstract void glGetShaderPrecisionFormat(
            int shadertype,
            int precisiontype,
            int[] range,
            int rangeOffset,
            int[] precision,
            int precisionOffset
    );

    // C function void glGetShaderPrecisionFormat ( GLenum shadertype, GLenum precisiontype, GLint *range, GLint *precision )

    public abstract void glGetShaderPrecisionFormat(
            int shadertype,
            int precisiontype,
            java.nio.IntBuffer range,
            java.nio.IntBuffer precision
    );

    // C function void glGetShaderSource ( GLuint shader, GLsizei bufsize, GLsizei *length, char *source )

    public abstract void glGetShaderSource(
            int shader,
            int bufsize,
            int[] length,
            int lengthOffset,
            byte[] source,
            int sourceOffset
    );

    // C function void glGetShaderSource ( GLuint shader, GLsizei bufsize, GLsizei *length, char *source )

    /** @hide Method is broken, but used to be public (b/6006380) */
    public abstract void glGetShaderSource(
            int shader,
            int bufsize,
            java.nio.IntBuffer length,
            byte source
    );

    // C function void glGetShaderSource ( GLuint shader, GLsizei bufsize, GLsizei *length, char *source )

    public abstract String glGetShaderSource(
            int shader
    );
    // C function const GLubyte * glGetString ( GLenum name )

    public abstract String glGetString(
            int name
    );
    // C function void glGetTexParameterfv ( GLenum target, GLenum pname, GLfloat *params )

    public abstract void glGetTexParameterfv(
            int target,
            int pname,
            float[] params,
            int offset
    );

    // C function void glGetTexParameterfv ( GLenum target, GLenum pname, GLfloat *params )

    public abstract void glGetTexParameterfv(
            int target,
            int pname,
            java.nio.FloatBuffer params
    );

    // C function void glGetTexParameteriv ( GLenum target, GLenum pname, GLint *params )

    public abstract void glGetTexParameteriv(
            int target,
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetTexParameteriv ( GLenum target, GLenum pname, GLint *params )

    public abstract void glGetTexParameteriv(
            int target,
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glGetUniformfv ( GLuint program, GLint location, GLfloat *params )

    public abstract void glGetUniformfv(
            int program,
            int location,
            float[] params,
            int offset
    );

    // C function void glGetUniformfv ( GLuint program, GLint location, GLfloat *params )

    public abstract void glGetUniformfv(
            int program,
            int location,
            java.nio.FloatBuffer params
    );

    // C function void glGetUniformiv ( GLuint program, GLint location, GLint *params )

    public abstract void glGetUniformiv(
            int program,
            int location,
            int[] params,
            int offset
    );

    // C function void glGetUniformiv ( GLuint program, GLint location, GLint *params )

    public abstract void glGetUniformiv(
            int program,
            int location,
            java.nio.IntBuffer params
    );

    // C function GLint glGetUniformLocation ( GLuint program, const char *name )

    public abstract int glGetUniformLocation(
            int program,
            String name
    );

    // C function void glGetVertexAttribfv ( GLuint index, GLenum pname, GLfloat *params )

    public abstract void glGetVertexAttribfv(
            int index,
            int pname,
            float[] params,
            int offset
    );

    // C function void glGetVertexAttribfv ( GLuint index, GLenum pname, GLfloat *params )

    public abstract void glGetVertexAttribfv(
            int index,
            int pname,
            java.nio.FloatBuffer params
    );

    // C function void glGetVertexAttribiv ( GLuint index, GLenum pname, GLint *params )

    public abstract void glGetVertexAttribiv(
            int index,
            int pname,
            int[] params,
            int offset
    );

    // C function void glGetVertexAttribiv ( GLuint index, GLenum pname, GLint *params )

    public abstract void glGetVertexAttribiv(
            int index,
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glHint ( GLenum target, GLenum mode )

    public abstract void glHint(
            int target,
            int mode
    );

    // C function GLboolean glIsBuffer ( GLuint buffer )

    public abstract boolean glIsBuffer(
            int buffer
    );

    // C function GLboolean glIsEnabled ( GLenum cap )

    public abstract boolean glIsEnabled(
            int cap
    );

    // C function GLboolean glIsFramebuffer ( GLuint framebuffer )

    public abstract boolean glIsFramebuffer(
            int framebuffer
    );

    // C function GLboolean glIsProgram ( GLuint program )

    public abstract boolean glIsProgram(
            int program
    );

    // C function GLboolean glIsRenderbuffer ( GLuint renderbuffer )

    public abstract boolean glIsRenderbuffer(
            int renderbuffer
    );

    // C function GLboolean glIsShader ( GLuint shader )

    public abstract boolean glIsShader(
            int shader
    );

    // C function GLboolean glIsTexture ( GLuint texture )

    public abstract boolean glIsTexture(
            int texture
    );

    // C function void glLineWidth ( GLfloat width )

    public abstract void glLineWidth(
            float width
    );

    // C function void glLinkProgram ( GLuint program )

    public abstract void glLinkProgram(
            int program
    );

    // C function void glPixelStorei ( GLenum pname, GLint param )

    public abstract void glPixelStorei(
            int pname,
            int param
    );

    // C function void glPolygonOffset ( GLfloat factor, GLfloat units )

    public abstract void glPolygonOffset(
            float factor,
            float units
    );

    // C function void glReadPixels ( GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, GLvoid *pixels )

    public abstract void glReadPixels(
            int x,
            int y,
            int width,
            int height,
            int format,
            int type,
            java.nio.Buffer pixels
    );

    // C function void glReleaseShaderCompiler ( void )

    public abstract void glReleaseShaderCompiler(
    );

    // C function void glRenderbufferStorage ( GLenum target, GLenum internalformat, GLsizei width, GLsizei height )

    public abstract void glRenderbufferStorage(
            int target,
            int internalformat,
            int width,
            int height
    );

    // C function void glSampleCoverage ( GLclampf value, GLboolean invert )

    public abstract void glSampleCoverage(
            float value,
            boolean invert
    );

    // C function void glScissor ( GLint x, GLint y, GLsizei width, GLsizei height )

    public abstract void glScissor(
            int x,
            int y,
            int width,
            int height
    );

    // C function void glShaderBinary ( GLsizei n, const GLuint *shaders, GLenum binaryformat, const GLvoid *binary, GLsizei length )

    public abstract void glShaderBinary(
            int n,
            int[] shaders,
            int offset,
            int binaryformat,
            java.nio.Buffer binary,
            int length
    );

    // C function void glShaderBinary ( GLsizei n, const GLuint *shaders, GLenum binaryformat, const GLvoid *binary, GLsizei length )

    public abstract void glShaderBinary(
            int n,
            java.nio.IntBuffer shaders,
            int binaryformat,
            java.nio.Buffer binary,
            int length
    );

    // C function void glShaderSource ( GLuint shader, GLsizei count, const GLchar ** string, const GLint* length )

    public abstract void glShaderSource(
            int shader,
            String... string
    );
    // C function void glStencilFunc ( GLenum func, GLint ref, GLuint mask )

    public abstract void glStencilFunc(
            int func,
            int ref,
            int mask
    );

    // C function void glStencilFuncSeparate ( GLenum face, GLenum func, GLint ref, GLuint mask )

    public abstract void glStencilFuncSeparate(
            int face,
            int func,
            int ref,
            int mask
    );

    // C function void glStencilMask ( GLuint mask )

    public abstract void glStencilMask(
            int mask
    );

    // C function void glStencilMaskSeparate ( GLenum face, GLuint mask )

    public abstract void glStencilMaskSeparate(
            int face,
            int mask
    );

    // C function void glStencilOp ( GLenum fail, GLenum zfail, GLenum zpass )

    public abstract void glStencilOp(
            int fail,
            int zfail,
            int zpass
    );

    // C function void glStencilOpSeparate ( GLenum face, GLenum fail, GLenum zfail, GLenum zpass )

    public abstract void glStencilOpSeparate(
            int face,
            int fail,
            int zfail,
            int zpass
    );

    // C function void glTexImage2D ( GLenum target, GLint level, GLint internalformat, GLsizei width, GLsizei height, GLint border, GLenum format, GLenum type, const GLvoid *pixels )

    public abstract void glTexImage2D(
            int target,
            int level,
            int internalformat,
            int width,
            int height,
            int border,
            int format,
            int type,
            java.nio.Buffer pixels
    );

    // C function void glTexParameterf ( GLenum target, GLenum pname, GLfloat param )

    public abstract void glTexParameterf(
            int target,
            int pname,
            float param
    );

    // C function void glTexParameterfv ( GLenum target, GLenum pname, const GLfloat *params )

    public abstract void glTexParameterfv(
            int target,
            int pname,
            float[] params,
            int offset
    );

    // C function void glTexParameterfv ( GLenum target, GLenum pname, const GLfloat *params )

    public abstract void glTexParameterfv(
            int target,
            int pname,
            java.nio.FloatBuffer params
    );

    // C function void glTexParameteri ( GLenum target, GLenum pname, GLint param )

    public abstract void glTexParameteri(
            int target,
            int pname,
            int param
    );

    // C function void glTexParameteriv ( GLenum target, GLenum pname, const GLint *params )

    public abstract void glTexParameteriv(
            int target,
            int pname,
            int[] params,
            int offset
    );

    // C function void glTexParameteriv ( GLenum target, GLenum pname, const GLint *params )

    public abstract void glTexParameteriv(
            int target,
            int pname,
            java.nio.IntBuffer params
    );

    // C function void glTexSubImage2D ( GLenum target, GLint level, GLint xoffset, GLint yoffset, GLsizei width, GLsizei height, GLenum format, GLenum type, const GLvoid *pixels )

    public abstract void glTexSubImage2D(
            int target,
            int level,
            int xoffset,
            int yoffset,
            int width,
            int height,
            int format,
            int type,
            java.nio.Buffer pixels
    );

    // C function void glUniform1f ( GLint location, GLfloat x )

    public abstract void glUniform1f(
            int location,
            float x
    );

    // C function void glUniform1fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform1fv(
            int location,
            int count,
            float[] v,
            int offset
    );

    // C function void glUniform1fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform1fv(
            int location,
            int count,
            java.nio.FloatBuffer v
    );

    // C function void glUniform1i ( GLint location, GLint x )

    public abstract void glUniform1i(
            int location,
            int x
    );

    // C function void glUniform1iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform1iv(
            int location,
            int count,
            int[] v,
            int offset
    );

    // C function void glUniform1iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform1iv(
            int location,
            int count,
            java.nio.IntBuffer v
    );

    // C function void glUniform2f ( GLint location, GLfloat x, GLfloat y )

    public abstract void glUniform2f(
            int location,
            float x,
            float y
    );

    // C function void glUniform2fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform2fv(
            int location,
            int count,
            float[] v,
            int offset
    );

    // C function void glUniform2fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform2fv(
            int location,
            int count,
            java.nio.FloatBuffer v
    );

    // C function void glUniform2i ( GLint location, GLint x, GLint y )

    public abstract void glUniform2i(
            int location,
            int x,
            int y
    );

    // C function void glUniform2iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform2iv(
            int location,
            int count,
            int[] v,
            int offset
    );

    // C function void glUniform2iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform2iv(
            int location,
            int count,
            java.nio.IntBuffer v
    );

    // C function void glUniform3f ( GLint location, GLfloat x, GLfloat y, GLfloat z )

    public abstract void glUniform3f(
            int location,
            float x,
            float y,
            float z
    );

    // C function void glUniform3fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform3fv(
            int location,
            int count,
            float[] v,
            int offset
    );

    // C function void glUniform3fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform3fv(
            int location,
            int count,
            java.nio.FloatBuffer v
    );

    // C function void glUniform3i ( GLint location, GLint x, GLint y, GLint z )

    public abstract void glUniform3i(
            int location,
            int x,
            int y,
            int z
    );

    // C function void glUniform3iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform3iv(
            int location,
            int count,
            int[] v,
            int offset
    );

    // C function void glUniform3iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform3iv(
            int location,
            int count,
            java.nio.IntBuffer v
    );

    // C function void glUniform4f ( GLint location, GLfloat x, GLfloat y, GLfloat z, GLfloat w )

    public abstract void glUniform4f(
            int location,
            float x,
            float y,
            float z,
            float w
    );

    // C function void glUniform4fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform4fv(
            int location,
            int count,
            float[] v,
            int offset
    );

    // C function void glUniform4fv ( GLint location, GLsizei count, const GLfloat *v )

    public abstract void glUniform4fv(
            int location,
            int count,
            java.nio.FloatBuffer v
    );

    // C function void glUniform4i ( GLint location, GLint x, GLint y, GLint z, GLint w )

    public abstract void glUniform4i(
            int location,
            int x,
            int y,
            int z,
            int w
    );

    // C function void glUniform4iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform4iv(
            int location,
            int count,
            int[] v,
            int offset
    );

    // C function void glUniform4iv ( GLint location, GLsizei count, const GLint *v )

    public abstract void glUniform4iv(
            int location,
            int count,
            java.nio.IntBuffer v
    );

    // C function void glUniformMatrix2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public abstract void glUniformMatrix2fv(
            int location,
            int count,
            boolean transpose,
            float[] value,
            int offset
    );

    // C function void glUniformMatrix2fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public abstract void glUniformMatrix2fv(
            int location,
            int count,
            boolean transpose,
            java.nio.FloatBuffer value
    );

    // C function void glUniformMatrix3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public abstract void glUniformMatrix3fv(
            int location,
            int count,
            boolean transpose,
            float[] value,
            int offset
    );

    // C function void glUniformMatrix3fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public abstract void glUniformMatrix3fv(
            int location,
            int count,
            boolean transpose,
            java.nio.FloatBuffer value
    );

    // C function void glUniformMatrix4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public abstract void glUniformMatrix4fv(
            int location,
            int count,
            boolean transpose,
            float[] value,
            int offset
    );

    // C function void glUniformMatrix4fv ( GLint location, GLsizei count, GLboolean transpose, const GLfloat *value )

    public abstract void glUniformMatrix4fv(
            int location,
            int count,
            boolean transpose,
            java.nio.FloatBuffer value
    );

    // C function void glUseProgram ( GLuint program )

    public abstract void glUseProgram(
            int program
    );

    // C function void glValidateProgram ( GLuint program )

    public abstract void glValidateProgram(
            int program
    );

    // C function void glVertexAttrib1f ( GLuint indx, GLfloat x )

    public abstract void glVertexAttrib1f(
            int indx,
            float x
    );

    // C function void glVertexAttrib1fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib1fv(
            int indx,
            float[] values,
            int offset
    );

    // C function void glVertexAttrib1fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib1fv(
            int indx,
            java.nio.FloatBuffer values
    );

    // C function void glVertexAttrib2f ( GLuint indx, GLfloat x, GLfloat y )

    public abstract void glVertexAttrib2f(
            int indx,
            float x,
            float y
    );

    // C function void glVertexAttrib2fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib2fv(
            int indx,
            float[] values,
            int offset
    );

    // C function void glVertexAttrib2fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib2fv(
            int indx,
            java.nio.FloatBuffer values
    );

    // C function void glVertexAttrib3f ( GLuint indx, GLfloat x, GLfloat y, GLfloat z )

    public abstract void glVertexAttrib3f(
            int indx,
            float x,
            float y,
            float z
    );

    // C function void glVertexAttrib3fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib3fv(
            int indx,
            float[] values,
            int offset
    );

    // C function void glVertexAttrib3fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib3fv(
            int indx,
            java.nio.FloatBuffer values
    );

    // C function void glVertexAttrib4f ( GLuint indx, GLfloat x, GLfloat y, GLfloat z, GLfloat w )

    public abstract void glVertexAttrib4f(
            int indx,
            float x,
            float y,
            float z,
            float w
    );

    // C function void glVertexAttrib4fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib4fv(
            int indx,
            float[] values,
            int offset
    );

    // C function void glVertexAttrib4fv ( GLuint indx, const GLfloat *values )

    public abstract void glVertexAttrib4fv(
            int indx,
            java.nio.FloatBuffer values
    );

    // C function void glVertexAttribPointer ( GLuint indx, GLint size, GLenum type, GLboolean normalized, GLsizei stride, GLint offset )

    public abstract void glVertexAttribPointer(
            int indx,
            int size,
            int type,
            boolean normalized,
            int stride,
            int offset
    );

    // C function void glVertexAttribPointer ( GLuint indx, GLint size, GLenum type, GLboolean normalized, GLsizei stride, const GLvoid *ptr )

    public abstract void glVertexAttribPointer(
            int indx,
            int size,
            int type,
            boolean normalized,
            int stride,
            java.nio.Buffer ptr
    );

    // C function void glViewport ( GLint x, GLint y, GLsizei width, GLsizei height )

    public abstract void glViewport(
            int x,
            int y,
            int width,
            int height
    );

}
