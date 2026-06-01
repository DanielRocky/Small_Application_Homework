package com.DanielRocky.small_application.model;


import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;


/**
 * entity model for representing a lecture node within the Neo4j graph
 */
@Node ("Lecture")
public class Lecture {

    @Id
    @Property("id")
    private String id;

    @Property
    private String topic;

    @Property("ects")
    private Integer ects;

    // default no-args constructor required by spring data neo4j
    public Lecture(){}

    //getters and setters
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getTopic(){
        return topic;
    }
    public void setTopic(String topic){
        this.topic = topic;
    }

    public Integer getEcts(){
        return ects;
    }

    public void setEcts(Integer ects){
        this.ects = ects;
    }
}
