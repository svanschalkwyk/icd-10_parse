/**
  * Created by steph on 4/6/17.
  */
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ArrayBuffer
import scala.xml._


object XPathTest extends App {
  val filename = "/home/steph/Documents/SSM/2016-CM/Index.xml"

  class term() {
    var level: Int = -1
    var title: String = ""
    var code: String = ""
    var see: String = ""
    var seeAlso: String = ""
  }

  var allCodes = List[term]()
  val maxIndex = 20
  //var tempTerm = new term()
  var tempTerms = new ArrayBuffer[term](maxIndex)


  val source = scala.io.Source.fromFile(filename)
  //val lines = try source.mkString finally source.close()
  var xml = scala.xml.XML.loadFile(filename)

  val mainTerms = (xml \\ "mainTerm")
  recurseTerms(mainTerms)
  //  pickleCodes()
  writeJSON()

  // not called anymore
  def getMainTerms(xml: Elem): Unit = {
    (xml \\ "mainTerm").foreach { mainTerm =>
      val title = (mainTerm \ "title").toString()
      val see = (mainTerm \ "see").toString()
      val seeAlso = (mainTerm \ "seeAlso").toString()
      //println(title + " " + see + " " + seeAlso)
      val terms = mainTerm \ "term"
      if (terms.length > 0)
        recurseTerms(terms)
    }
  }

  def recurseTerms(terms: NodeSeq): Unit = {
    terms.foreach({ term =>
      val level = if ((term \ "@level").text == "") 0 else (term \ "@level").text.toInt
      if (level == 0) {
        tempTerms.clear()
      } // start new chain
    val tempTerm = new term()
      tempTerm.level = level
      tempTerm.title = (term \ "title").text
      tempTerm.code = (term \ "code").text
      tempTerm.see = (term \ "see").text
      tempTerm.seeAlso = (term \ "seeAlso").text
      if (level > 0) {
        //          tempTerm.title = tempTerms(level - 1).title + "+" + tempTerm.title
        tempTerm.title = tempTerms(level - 1).title + " " + tempTerm.title
      }
      tempTerms.drop(level)
      tempTerms.insert(level, tempTerm)
      allCodes = allCodes :+ tempTerm
      //        println(tempTerms(level).code + " " + tempTerms(level).title)
      println(tempTerms(level).title + " " + tempTerms(level).code )
      writeJSON(tempTerm)
      val length = (term \ "term").length

      if ((term \ "term").length > 0) {
        recurseTerms(term \ "term")
      }
      else {
        recurseTerms(term \ "..")
      }
    })
  }

  def writeJSON(): Unit = {
    allCodes.foreach({ icd => {
      val json =
        JObject(
          ("title" -> JString(icd.title)),
          ("code" -> JString(icd.code)),
          ("see" -> JString(icd.see)),
          ("seeAlso" -> JString(icd.seeAlso)),
          ("level" -> JInt(icd.level))
        )
      println(compact(render(json)))
    }
    })
  }

  def writeJSON(icd: term): Unit = {
    val json =
      JObject(
        ("title" -> JString(icd.title)),
        ("code" -> JString(icd.code)),
        ("see" -> JString(icd.see)),
        ("seeAlso" -> JString(icd.seeAlso)),
        ("level" -> JInt(icd.level))
      )
    println(compact(render(json)))
  }







}