# Unrefined
Version Name: 0.9.0  
Version Code: 91  
Version Codename: Claw Hammer

Cross-platform multimedia application framework written in pure Java.

This library is designed for multimedia application, but it not just a multimedia application framework.  
You can also use it as a game engine or a utility toolkit.

It provides:  
[Console API](/core/src/main/java/unrefined/io/console)  
[Window & Widget Management API](/core/src/main/java/unrefined/context)  
[Graphics API that 90% compatible with HTML5 &lt;canvas&gt;](/core/src/main/java/unrefined/media/graphics)  
[Music & Sound Effects API](/core/src/main/java/unrefined/media/sound)  
[Memory Management API](/core/src/main/java/unrefined/nio)  
[Qt-like Signal/Slot API](/core/src/main/java/unrefined/util/signal)  
[Pub/sub Event Bus API](/core/src/main/java/unrefined/util/event)  
[Functional Programming Helper API](/core/src/main/java/unrefined/util/function/Function.java)  
[Extended Collections API](/core/src/main/java/unrefined/util/)  
[Extended Math API](/core/src/main/java/unrefined/math/)  
[Animation API](/core/src/main/java/unrefined/util/animation)  
[Serialization/Deserialization API](/core/src/main/java/unrefined/io/)  
[Preferences API](/core/src/main/java/unrefined/app/Preferences.java)  
[Extended Properties & INI API](/core/src/main/java/unrefined/util/HierarchicalProperties.java)  
[JSON API](/core/src/main/java/unrefined/json/)  
[XML DOM/SAX/StAX/XPath API](/core/src/main/java/unrefined/xml/)  
[FileSystem API](/core/src/main/java/unrefined/io/file)  
[Log API](/core/src/main/java/unrefined/app/Log.java)  
[Net API](/core/src/main/java/unrefined/net)  
[MVVM Data Binding API](/core/src/main/java/unrefined/beans)  
[Foreign Function Interface API](/core/src/main/java/unrefined/util/foreign)  
[Runtime API](/core/src/main/java/unrefined/app/Runtime.java)  
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
