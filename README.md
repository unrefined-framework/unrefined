# Unrefined
Version Name: 0.7.0  
Version Code: 71  
Version Codename: Claw Hammer

Cross-platform multimedia framework written in pure Java.

This library is designed for multimedia, but not just a multimedia framework.  
You can also use it as a game engine or a utility toolkit.

It provides:  
[Extended Console API](/base/src/main/java/unrefined/io/console)  
[Window & Widget Management API](/base/src/main/java/unrefined/context)  
[Feature-rich 2D Graphics API](/base/src/main/java/unrefined/media/graphics)  
[Extended Memory Management API](/base/src/main/java/unrefined/nio)  
[Qt-like Signal/Slot API](/base/src/main/java/unrefined/util/signal)  
[Pub/sub Event Bus API](/base/src/main/java/unrefined/util/event)  
[Functional Programming Helper API](/base/src/main/java/unrefined/util/function/Function.java)  
[Extended Collections API](/base/src/main/java/unrefined/util/)  
[Animation API](/base/src/main/java/unrefined/util/animation)  
[Serialization/Deserialization API](/base/src/main/java/unrefined/io/Savable.java)  
[Logger API](/base/src/main/java/unrefined/app/Logger.java)  
[MVVM Data Binding API](/base/src/main/java/unrefined/beans)  
[Foreign Function Interface API](/base/src/main/java/unrefined/util/foreign)  
[Extended Runtime API](/base/src/main/java/unrefined/app/Runtime.java)  
...and more

It is also designed to work properly in "headless mode".

## Compatibility
| Backend | Compatibility |
|---------|---------------|
| Desktop | Java SE 8     |

## Documentation
[Wiki](https://github.com/Tianscar/unrefined/wiki)

## Contributing
[Roadmap](/TODO)  
Pull Requests & Issues welcome!

## License
[Apache-2.0](/LICENSE)

### Dependencies
| Library                                                                                      | License                                                                             | Comptime | Runtime |
|----------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|----------|---------|
| [JFFI](https://github.com/jnr/jffi)                                                          | Apache-2.0                                                                          | Yes      | Yes     |
| [JavaSound Resource Loader](https://github.com/Tianscar/javasound-resloader)                 | MIT                                                                                 | Yes      | Yes     |
| [JavaSound MP3](https://github.com/Tianscar/javasound-mp3)                                   | LGPL-2.1                                                                            | Yes      | Yes     |
| [Tritonus Share](https://mvnrepository.com/artifact/com.googlecode.soundlibs/tritonus-share) | LGPL-2.1                                                                            | Yes      | Yes     |
| [JavaSound FLAC](https://github.com/Tianscar/javasound-flac)                                 | LGPL-2.1, Apache-2.0, Xiph.Org Variant of the BSD License                           | Yes      | Yes     |
| [JavaSound AAC](https://github.com/Tianscar/javasound-aac)                                   | Public Domain                                                                       | Yes      | Yes     |
| [J-OGG Vorbis](https://github.com/stephengold/j-ogg-all)                                     | [An Informal License](https://github.com/stephengold/j-ogg-all/blob/master/LICENSE) | Yes      | Yes     |
