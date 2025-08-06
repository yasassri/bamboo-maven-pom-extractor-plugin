# bamboo-maven-pom-extractor-plugin

Upgraded version of the `bamboo-maven-pom-extractor-plugin` plugin from Bamboo version 6.10.6 to support Bamboo 10.x versions. Full credit to the original developer for developping this Plugin, I have migrated this as a personal project and sharing so others can benefit from it.

Also moving this code from original location https://bitbucket.org/dehringer/bamboo-maven-pom-extractor-plugin to Github for better accessibility and maintenance.

## Download and Install
You can download the latest version of the plugin from the [Releases](https://github.com/yasassri/bamboo-maven-pom-extractor-plugin/releases) section of this repository. Go to the Releases page and download the .jar file for the version you want to use. Once downloaded you can install this to Bamboo as a custom plugin. If you have migrated from a older Bamboo version with older plugin, re-installing this plugin will make sure your Jobs work seamlessly. 

## Main changes done

### Bamboo 10.2+ Compatibility Updates

1. Removed Deprecated APIs
2. Plugin upgrades
3. Updated Dependencies
   - Java 17
   - Bamboo SDK 10.2.0
   - Standard Java collections instead of Guava etc. 

