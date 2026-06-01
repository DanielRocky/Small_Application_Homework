package com.DanielRocky.small_application.model;


import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * entity model for representing a student node within the Neo4j graph
 */
@Node("Student")
public class Student {

    @Id
    @Property("matriculationNumber")
    private String matriculationNumber;

    @Property("name")
    private String name;

    // default no-args constructor required by spring data neo4j
    public Student() {}

    public Student(String matriculationNumber, String name){
        this.matriculationNumber = matriculationNumber;
        this.name = name;
    }

    // getters and setters
    public String getMatriculationNumber(){
        return matriculationNumber;
    }
    public void setMatriculationNumber(){
        this.matriculationNumber = matriculationNumber;
    }

    public String getName(){
        return name;
    }
    public void setName(){
        this.name = name;
    }
}
