package net.abhinavsarkar.spelhelper

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import java.util.{HashSet, Set => JSet, List => JList, ArrayList}

@RunWith(classOf[JUnitRunner])
class ImplicitMethodsSpec extends FlatSpec with ShouldMatchersForJUnit {

  "Implicit Function 'distinct' on List" should
      "return distinct items in a list " in {
    val set: JSet[String] = new HashSet
    set add "a"; set add "b"
    new SpelHelper().evalExpression("#list('a','b','a').distinct()",
      new {}, classOf[JSet[String]]) should equal(set)
  }

  "Implicit Function 'sorted' on List" should
      "return a sorted list " in {
    val list: JList[String] = new ArrayList
    List("a", "b", "c") foreach { list add _ }
    new SpelHelper().evalExpression("#list('c','b','a').sorted()",
      new {}, classOf[JList[String]]) should equal(list)
  }

  "Implicit Function 'reversed' on List" should
      "return a reversed list " in {
    val list: JList[String] = new ArrayList
    List("a", "b", "c") foreach { list add _ }
    new SpelHelper().evalExpression("#list('c','b','a').reversed()",
      new {}, classOf[JList[String]]) should equal(list)
  }

  "Implicit Function 'take' on List" should
      "return a list containing first n items of a list " in {
    val list: JList[String] = new ArrayList
    List("a", "b", "c") foreach { list add _ }
    new SpelHelper().evalExpression("#list('a','b','c','d').take(3)",
      new {}, classOf[JList[String]]) should equal(list)
  }

  "Implicit Function 'drop' on List" should
      "return a list containing items after the first n items of a list " in {
    val list: JList[String] = new ArrayList
    List("c", "d") foreach { list add _ }
    new SpelHelper().evalExpression("#list('a','b','c','d').drop(2)",
      new {}, classOf[JList[String]]) should equal(list)
  }
  
}