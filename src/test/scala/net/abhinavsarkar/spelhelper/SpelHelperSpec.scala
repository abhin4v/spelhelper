package net.abhinavsarkar.spelhelper

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.ShouldMatchersForJUnit
import org.springframework.expression.spel.SpelEvaluationException

@RunWith(classOf[JUnitRunner])
class SpelHelperSpec extends FlatSpec with ShouldMatchersForJUnit {

  "SpelHelper" should "register and evaluate functions " in {
    new SpelHelper()
      .registerFunctionsFromClass(classOf[Functions])
      .evalExpression(
        "#test('check')", new {}, classOf[String]) should equal ("check")
  }

  it should "not register non public methods " in {
    val spelHelper = new SpelHelper()
      .registerFunctionsFromClass(classOf[Functions])
    evaluating { spelHelper.evalExpression("#testNonPublic('check')",
      new {}, classOf[String]) } should produce [SpelEvaluationException]
  }

  it should "not register non static methods " in {
    val spelHelper = new SpelHelper()
      .registerFunctionsFromClass(classOf[Functions])
    evaluating { spelHelper.evalExpression("#testNonStatic('check')",
      new {}, classOf[String]) } should produce [SpelEvaluationException]
  }

  it should "not register void methods " in {
    val spelHelper = new SpelHelper()
      .registerFunctionsFromClass(classOf[Functions])
    evaluating { spelHelper.evalExpression("#testVoid('check')",
      new {}, classOf[String]) } should produce [SpelEvaluationException]
  }

  it should "register implicit methods " in {
    new SpelHelper()
      .registerImplicitMethodsFromClass(classOf[Functions])
      .lookupImplicitMethod("java.lang.String.test") should equal(
        classOf[Functions].getMethod("test", classOf[String]))
  }

  it should "not register methods with no args as implicit methods " in {
    new SpelHelper()
      .registerImplicitMethodsFromClass(classOf[Functions])
      .lookupImplicitMethod("java.lang.String.testNoArg") should be (null);
  }

  it should "register implicit constructors " in {
    new SpelHelper()
      .registerConstructorsFromClass(classOf[Functions])
      .lookupImplicitConstructor("Functions[]") should equal(
        classOf[Functions].getConstructor())
  }

  it should "evaluate implicit methods " in {
    new SpelHelper()
      .registerImplicitMethodsFromClass(classOf[Functions])
      .evalExpression(
        "'check'.test()", new {}, classOf[String]) should equal ("check")
  }

  it should "evaluate implicit constructors " in {
    new SpelHelper()
      .registerConstructorsFromClass(classOf[Functions])
      .evalExpression(
        "new Functions()", new {}, classOf[Functions]) should equal (new Functions)
  }

  it should "evaluate implicit properties " in {
    new SpelHelper().evalExpression(
      "'abc'.hashCode", new {}, classOf[int]) should equal ("abc".hashCode)
  }

  it should "evaluate multiple expressions " in {
    new SpelHelper().evalExpressions(
      Array("#s='check'", "#s"), new {}, classOf[String]) should equal ("check")
  }

  it should "throw IllegalArgumentException when trying to evaluate " +
          "blank multiple expressions " in {
    evaluating { new SpelHelper().evalExpressions(
      Array[String](), new {}, classOf[String]) } should produce [IllegalArgumentException]
  }

  it should "return evaluation context inside a method called " +
          "from SpEL expression " in {
    new SpelHelper()
      .registerFunctionsFromClass(classOf[Functions])
      .evalExpression(
        "#testContext('check')", new {}, classOf[String]) should equal ("check")
  }

  it should "not return evaluation context outside a method called " +
          "from SpEL expression " in {
    SpelHelper.getCurrentContext should be (null)
  }
  
}
