package com.ohs.monolithic.common.utils;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.MySQL8Dialect;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.type.BasicType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.StandardBasicTypes;

public class MySQLDialectCustom extends MySQLDialect {

  @Override
  public void initializeFunctionRegistry(FunctionContributions functionContributions){
    super.initializeFunctionRegistry(functionContributions);

    BasicType<Double> doubleType = functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.DOUBLE);
    functionContributions.getFunctionRegistry().registerPattern(
            // 함수의 이름을 설정한다.
            "match_against",
            // 함수가 어떻게 동작하는지 패턴으로 작성한다.
            // ?1, ?2와 같이 매핑할 수 있다.
            "MATCH (?1) AGAINST (?2)", doubleType
            //"match(?1) against (?2 in boolean mode)", doubleType
            // 이 함수가 리턴하는 타입을 자바 형태로 작성한다.
            /*functionContributions.getTypeConfiguration()
                    .getBasicTypeForJavaType(Boolean.class)*/
    );

    functionContributions.getFunctionRegistry().registerPattern(
            "match_against_booleanmode",
            "MATCH (?1) AGAINST (?2 IN BOOLEAN MODE) ", doubleType
    );
  }

/*  public MySQLDialectCustom(){
    super();
    StandardBasicTypes.DOUBLE
    *//*registerFunction(
            "match",
            new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "match(?1) against (?2 in boolean mode)")
    );*//*
  }*/
}
