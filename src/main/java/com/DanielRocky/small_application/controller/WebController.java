package com.DanielRocky.small_application.controller;

import com.DanielRocky.small_application.model.Lecture;
import com.DanielRocky.small_application.model.Student;
import com.DanielRocky.small_application.repository.LectureRepository;
import com.DanielRocky.small_application.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * controller responsible for handling incoming client web requests,
 * coordinating graph and returns the model data to the UI
 */
@Controller
public class WebController {

    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;

    public WebController(LectureRepository lectureRepository, StudentRepository studentRepository) {
        this.lectureRepository = lectureRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * add course participants(with an existing entity
     * maps user input properties to dynamically bind a student to a lecture
     */
    @PostMapping("/add-participant")
    public String addParticipant(@RequestParam String lectureId,
                                 @RequestParam String searchBy,
                                 @RequestParam String identifier){
        // evaluate input flag to decide if lookup runs via matriculation number or name
        if ("matNr".equals(searchBy)){
            studentRepository.addStudentToLectureByMatNr(identifier, lectureId);
        } else {
            studentRepository.addStudentToLectureByName(identifier, lectureId);
        }
        return "redirect:/participants?lectureId=" + lectureId;
    }

    /**
     * delete/remove course participants
     */
    @PostMapping("/remove-participant")
    public String removeParticipant(@RequestParam String lectureId,
                                    @RequestParam String removeBy,
                                    @RequestParam String identifier){
        // removes the targeted :HEARS graph relationship (matriculation number or name)
        if ("matNr".equals(removeBy)) {
            studentRepository.removeStudentFromLectureByMatNr(identifier, lectureId);
        } else {
            studentRepository.removeStudentFromLectureByName(identifier, lectureId);
        }
        return "redirect:/participants?lectureId=" + lectureId;
    }

    /**
     * lecture search module
     */

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String searchString){
        // if search String is present, triggers multi property index scanning
        if (searchString != null && !searchString.isEmpty()) {
            List<Lecture> results = lectureRepository.searchLecturesByString(searchString);
            model.addAttribute("lectures", results);
            model.addAttribute("searchString", searchString);
        }
        return "index"; // directs to resources/templates/index.html
    }

    /**
     * determines semantic relationship connection between person A and B
     */
    @GetMapping("/check-connection")
    public String checkConnection(Model model, @RequestParam String personA, @RequestParam String personB){
        String connection = studentRepository.checkConnectionBetweenPeople(personA, personB);
        model.addAttribute("personA", personA);
        model.addAttribute("personB", personB);
        model.addAttribute("connectionResult", connection);
        return "index";
    }

    /**
     * asks for a lecture number and list its active student participants
     * sorts students in ascending order on the database server side
     */
    @GetMapping("/participants")
    public String viewParticipants(Model model, @RequestParam String lectureId) {
        List<Student> participants = lectureRepository.getParticipantsAscending(lectureId);
        model.addAttribute("selectedLectureId", lectureId);
        model.addAttribute("participants", participants);
        return "index";
    }

    /**
     * deletes a student
     * runs a transaction that executes a DETACH DELETE followed by a :Notification creation
     */
    @PostMapping("/delete-student")
    public String deleteStudent(Model model,
                                @RequestParam String deleteBy,
                                @RequestParam String identifier,
                                @RequestParam String reason){
        if ("matNr".equals(deleteBy)) {
            studentRepository.deleteByMatriculationNumberAndNotify(identifier, reason);
            model.addAttribute("deleteMessage", "Student with MatriculationNumber '" + identifier + "' deleted (Notification created).");
        } else {
            studentRepository.deleteByNameAndNotify(identifier, reason);
            model.addAttribute("deleteMessage", "Student with name '" + identifier + "' deleted (Notification created).");
        }
        return "index";
    }

    /**
     * grade student if the student is registered
     */
    @PostMapping("/grade-student")
    public String gradeStudent(Model model,
                               @RequestParam String matNr,
                               @RequestParam String lectureId,
                               @RequestParam String examDate,
                               @RequestParam Integer grade) {
        boolean success = studentRepository.gradeStudentIfRegistered(matNr, lectureId, examDate, grade);
        if (success) {
            model.addAttribute("gradeMessage", "Succes: The grade " + grade + " entered for " + matNr + " !");
            model.addAttribute("gradeSuccess", true);
        } else {
            model.addAttribute("gradeMessage", "Error: The student is not registered for the exam :( !");
            model.addAttribute("gradeSuccess", false);
        }
        return "index";
    }

    /**
     * displays any relationship path from shortest to longest
     */
    @GetMapping("/find-paths")
    public String findPaths(Model model, @RequestParam String nodeA, @RequestParam String nodeB) {
        List<String> paths = studentRepository.findPathsBetweenNodes(nodeA, nodeB);
        model.addAttribute("nodeA", nodeA);
        model.addAttribute("nodeB", nodeB);
        model.addAttribute("pathsResult", paths);
        return "index";
    }
}
