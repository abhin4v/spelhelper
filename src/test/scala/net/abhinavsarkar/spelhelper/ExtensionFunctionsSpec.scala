package net.abhinavsarkar.spelhelper

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import java.util.{Set => JSet, HashSet,
  List => JList, ArrayList,
  Map => JMap, HashMap}
import org.springframework.expression.spel.SpelEvaluationException

@RunWith(classOf[JUnitRunner])
class ExtensionFunctionsSpec extends FlatSpec with ShouldMatchersForJUnit {

  "Extension Function 'list'" should "return a java.util.List " in {
    val list: JList[String] = new ArrayList
    List("a", "b", "c") foreach { list add _ }
    new SpelHelper().evalExpression("#list('a','b','c')",
      new {}, classOf[JList[String]]) should equal(list)
  }

  "Extension Function 'set'" should "return a java.util.Set " in {
    val set: JSet[String] = new HashSet
    List("a", "b", "c") foreach { set add _ }
    new SpelHelper().evalExpression("#set('a','b','c')",
      new {}, classOf[JSet[String]]) should equal(set)
  }

  "Extension Function 'map'" should "return a java.util.Map " in {
    val map: JMap[String,Int] = new HashMap
    List("a", "b", "c").zipWithIndex.foreach { x => map.put(x._1, x._2) }
    new SpelHelper().evalExpression("#map(#list('a','b','c'),#list(0,1,2))",
      new {}, classOf[JMap[String,Int]]) should equal(map)
  }

  "Extension Function 'map'" should "throw SpelEvaluationException" +
          "if length of key and values lists is not same " in {
    evaluating { new SpelHelper().evalExpression("#map(#list('a','b','c'),#list(1,2))",
      new {}, classOf[JMap[String,Int]]) } should produce [SpelEvaluationException]
  }

}
