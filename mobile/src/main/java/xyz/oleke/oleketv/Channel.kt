/*
*@author: Ogunleke Abiodun
* A simple model to store channel information
 */
package xyz.oleke.oleketv

/*
   Class define with constructors
 */
class Channel(name_: String, url_: String, icon_: String) {
    private val name = name_ //Channel name
    private val url = url_ //URL to Channel
    private val icon = icon_ //Channel Icon


    fun getName(): String {
        return name
    }


    fun getURL(): String {
        return url
    }

    fun getIcon(): String {
        return icon
    }


}