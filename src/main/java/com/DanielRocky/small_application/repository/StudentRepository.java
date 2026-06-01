package com.DanielRocky.small_application.repository;


import com.DanielRocky.small_application.model.Student;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StudentRepository that handles all structural graph database operations for student nodes
 */
@Repository
public interface StudentRepository extends Neo4jRepository<Student, String> {

    /**
     * manage course participants(add existing entity by matriculation number)
     */
    @Query("MATCH (s:Student {matriculationNumber: $matNr}), (l:Lecture {id: $lectureId}) "+
            "MERGE (s)-[:HEARS]->(l)")
    void addStudentToLectureByMatNr(@Param("matNr") String matNr, @Param("lectureId") String lectureId);


    /**
     * manage course participants(add existing entity by student name)
     */
    @Query("MATCH (s:Student {name: $name}), (l:Lecture {id: $lectureId}) "+
            "MERGE (s)-[:HEARS]->(l)")
    void addStudentToLectureByName(@Param("name") String name, @Param("lectureId") String lectureId);

    /**
     * manage course participants(deletes specific relationship by matriculation number)
     */
    @Query("MATCH (s:Student {matriculationNumber: $matNr})-[r:HEARS]->(l:Lecture {id: $lectureId}) "+
            "DELETE r")
    void removeStudentFromLectureByMatNr(@Param("matNr") String matNr, @Param("lectureId") String lectureId);

    /**
     * manage course participants(deletes specific relationship by student name)
     */
    @Query("MATCH (s:Student {name: $name})-[r:HEARS]->(l:Lecture {id: $lectureId}) " +
            "DELETE r")
    void removeStudentFromLectureByName(@Param("name") String name, @Param("lectureId") String lectureId);

    /**
     * deletes a student by matriculation number and automatically creates a notification node
     */
    @Query("MATCH (s:Student {matriculationNumber: $matNr}) " +
            "CREATE (n:Notification {type: 'Deletion', timestamp: datetime(), reason: $reason, info: s.name}) " +
            "DETACH DELETE s")
    void deleteByMatriculationNumberAndNotify(@Param("matNr") String matNr, @Param("reason") String reason);

    /**
     * deletes a student by name and automatically creates a notification node
     */
    @Query("MATCH (s:Student {name: $name}) " +
            "CREATE (n:Notification {type: 'Student Deletion', timestamp: datetime(), reason: $reason, deletedName: s.name, deletedId: s.matriculationNumber}) " +
            "DETACH DELETE s")
    void deleteByNameAndNotify(@Param("name") String name, @Param("reason") String reason);

    /**
     * grades a student for an exam
     * makes sure that if student is not registered, that they can not be graded
     */
    @Query("MATCH (s:Student {matriculationNumber: $matNr}) - [r:REGISTERS] -> (e:Exam {date: $examDate}) <- [:HAS_EXAM] - (l:Lecture {id: $lectureId}) " +
            "MERGE (s)-[g:HAS_GRADE]->(e) " +
            "SET g.grade = $grade "+
            "RETURN count(g) > 0")
    boolean gradeStudentIfRegistered(
            @Param("matNr") String matNr,
            @Param("lectureId") String lectureId,
            @Param("examDate") String examDate,
            @Param("grade") Integer grade
    );

    /**
     * determine connection between A and B
     * classmates if same lecture
     * colleagues if same professor
     * else no connection
     */
    @Query("MATCH (a {name: $nameA}), (b {name: $nameB}) "+
            "OPTIONAL MATCH (a)-[:HEARS]->(l1:Lecture)<-[:HEARS]-(b) "+
            "OPTIONAL MATCH (a)-[:TEACHES]->(l2:Lecture)<-[:TEACHES]-(b) "+
            "RETURN CASE "+
            "  WHEN l1 IS NOT NULL AND 'Student' IN labels(a) AND 'Student' IN labels(b) THEN 'classmates' "+
            "  WHEN l2 IS NOT NULL AND 'Professor' IN labels(a) AND 'Professor' IN labels(b) THEN 'colleagues' "+
            "  ELSE 'no connection' "+
            "END LIMIT 1")
    String checkConnectionBetweenPeople(@Param("nameA") String nameA, @Param("nameB") String nameB);

    /**
     * displays all paths connections of A and B
     * lists the shortest path at the beginning and the longest path at the end
     */
    @Query("MATCH p = (a {name: $nameA})-[*1..4]-(b {name: $nameB}) " +
            "RETURN reduce(s = coalesce(a.name,a.id,''), n IN tail(nodes(p)) | " +
            "  s + ' <--> ' + coalesce(n.name, n.id,n.topic, 'Unknown')) AS pathString " +
            "ORDER BY length(p) ASC")
    List<String> findPathsBetweenNodes(@Param("nameA") String nameA, @Param("nameB") String nameB);
}
