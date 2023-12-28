package com.ohs.monolithic;


import org.junit.platform.suite.api.*;


@Suite
@SuiteDisplayName("기본 - 유닛 테스트")
@SelectPackages("com.ohs.monolithic") // 이걸 지정해야, 테스트 클래스들을 찾을 수 있다.
//@IncludeTags({"base&integrate", "unit"}) // base or unit
@IncludeTags({"base & unit", "base & integrate-limited", "base & integrate"})
//@ExcludeTags({"integrate"}) // integrate or ...
//@ConfigurationParameter(key = FILTER_TAGS_INCLUDE_PATTERN, value = "yourTag")
public class BaseTests {

}
