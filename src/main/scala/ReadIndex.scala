import scala.io.Source
import scala.xml._
import scala.xml.pull._
/**
  * Created by steph on 4/4/17.
  */

object ElementFlags extends Enumeration {
  type ElementFlags = Value
  val Title, Code, See, SeeAlso, None = Value
}

object ReadIndex extends App {

  import ElementFlags._

  val maxIndex = 20
  var level = 0
  var flags = None
  var mainTermFlag = false

  class term() {
    var level:String = ""
    var title: String = ""
    var code: String = ""
    var see: String = ""
    var seeAlso: String = ""
  }

  var terms = List[term]()
  var tempTerms = new Array[term](20)
  var tempTerm = new term()

  val filename = "/home/steph/Documents/SSM/2016-CM/Index.xml"
  val xml = new XMLEventReader(Source.fromFile(filename))
  xml.foreach(matchEvent)

  def resetArray(): Unit = {
    level = 0
    tempTerm = new term()
    tempTerms = new Array[term](20)
  }

  def matchEvent(ev: XMLEvent) {
    ev match {
      case EvElemStart(_, "letter", _, _) => {
      }
      case EvElemEnd(_, "letter") => {
      }
      case EvElemStart(_, "mainTerm", _, _) => {
        mainTermFlag = true
        resetArray()
      }
      case EvElemEnd(_, "mainTerm") => {
        mainTermFlag = false
//        tempTerm.level = "0"
//        tempTerms(0) = tempTerm
//        println(level)
//        terms = terms :+ tempTerm
//        println
      }
      case EvElemStart(_, "term", attr, _) => {
        if (mainTermFlag == true) { // add mainTerm tempTerm
          mainTermFlag = false
          tempTerm.level = "0"
          tempTerms(0) = tempTerm
          terms = terms :+ tempTerm
        }
        else {
          tempTerm.level = level.toString
          tempTerms(level) = tempTerm
          println(level)
          if (level > 0) {
            tempTerm.level = tempTerms(level - 1).level + " " + tempTerm.level
            tempTerm.title = tempTerms(level - 1).title + " " + tempTerm.title
          }
          terms = terms :+ tempTerm
        }
        level = termAttrs(attr)
        tempTerm = new term()
        println ("***************************************************")
        println("term:" + level)
      }
      case EvElemEnd(_, "term") => {
      }
      case EvElemStart(_, "title", _, _) => {
        flags = ElementFlags.Title
      }
      case EvElemEnd(_, "title") => {
        flags = ElementFlags.None
      }
      case EvElemStart(_, "see", _, _) => {
        flags = ElementFlags.See
      }
      case EvElemEnd(_, "see") => {
        flags = ElementFlags.None
      }
      case EvElemStart(_, "seeAlso", _, _) => {
        flags = ElementFlags.SeeAlso
      }
      case EvElemEnd(_, "seeAlso") => {
        flags = ElementFlags.None
      }
      case EvElemStart(_, "code", _, _) => {
        flags = ElementFlags.Code
      }
      case EvElemEnd(_, "code") => {
        flags = ElementFlags.None
      }
      case EvText(text) => {
        if (text.length > 0) {
          flags match {
            case ElementFlags.Title => {
              tempTerm.title += text + " "
            }
            case ElementFlags.Code => {
              tempTerm.code = text + " "
            }
            case ElementFlags.See => {
              tempTerm.see += text + " "
            }
            case ElementFlags.SeeAlso => {
              tempTerm.seeAlso += text + " "
            }
            case ElementFlags.None => {
            }
          }
        }
      }
      case EvElemStart(_, label, _, _) => {
        println(label)
      }
      case EvElemEnd(_, label) => {
        println(label)
      }

    }
  }
  def backToXml(ev: XMLEvent) = {
    ev match {
      case EvElemStart(pre, label, attrs, scope) => {
        "<" + label + attrsToString(attrs) + ">"
      }
      case EvElemEnd(pre, label) => {
        "</" + label + ">"
      }
      case _ => ""
    }
  }

  def termAttrs(attrs:MetaData):Int = {
    attrs.length match {
      case 0 => -1
      case _ =>   (attrs.value.toString()).toInt
//      case _ => attrs.map((m:MetaData) => m.value).toString()

    }
  }

  def attrsToString(attrs:MetaData) = {
    attrs.length match {
      case 0 => ""
      case _ => attrs.map( (m:MetaData) => " " + m.key + "='" + m.value +"'" ).reduceLeft(_+_)
    }
  }
//  def filterText(text: String) = {
//    val matches = interLink.findAllIn(text)
//    if (matches.hasNext) matches.reduceLeft(_+_) else ""
//  }


}
