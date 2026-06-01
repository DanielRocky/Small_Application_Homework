package com.DanielRocky.small_application.repository;

import com.DanielRocky.small_application.model.Lecture;
import com.DanielRocky.small_application.model.Student;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * data access repository for lecture node operations and participant aggregation
 */
@Repository
public interface LectureRepository extends Neo4jRepository<Lecture, String> {

    /**
     * display a list of all lectures based on provided search strings
     * it uses a case-insensitive search logic via toLower() and CONTAINS multiple node boundaries
     * OPTIONAL MATCH and coalesce() protect the traversal path from failing if relations are null
     */
    @Query("MATCH (l:Lecture) " +
            "OPTIONAL MATCH (p:Professor) - [:TEACHES] -> (l) "+
            "OPTIONAL MATCH (l) - [:HAS_EXAM] -> (e:Exam) "+
            "WITH l, p, e "+
            "WHERE toLower(l.id) CONTAINS toLower($search) "+
            "   OR toLower(l.topic) CONTAINS toLower($search) " +
            "   OR toLower(coalesce(p.name, '')) CONTAINS toLower($search) "+
            "   OR toLower(coalesce(e.room, '')) CONTAINS toLower($search) "+
            "   OR toLower(coalesce(e.date, '')) CONTAINS toLower($search) "+
            "RETURN DISTINCT l")
    List<Lecture> searchLecturesByString(@Param("search") String search);

    /**
     * asks for a lecture number and the list its participants in ascending order by name
     * it targets an explicit lecture node and extracts student nodes via :HEARS relationships
     */
    @Query("MATCH (s:Student)-[:HEARS]->(l:Lecture {id: $lectureId}) " +
            "RETURN s.matriculationNumber AS matriculationNumber, s.name AS name " +
            "ORDER BY s.name ASC")
    List<Student> getParticipantsAscending(@Param("lectureId") String lectureId);
}
