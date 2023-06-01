package com.spring.hibernate.manytomany.controller;

import java.util.ArrayList;
import java.util.List;

import com.spring.hibernate.manytomany.exception.ResourceNotFoundException;
import com.spring.hibernate.manytomany.repository.TagRepository;
import com.spring.hibernate.manytomany.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.hibernate.manytomany.model.Tag;
import com.spring.hibernate.manytomany.model.Tutorial;

//@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TagController {

  @Autowired
  private TutorialRepository tutorialRepository;

  @Autowired
  private TagRepository tagRepository;

  @GetMapping("/tags")
  public ResponseEntity<List<Tag>> getAllTags() {
    List<Tag> tags = new ArrayList<Tag>();

    tagRepository.findAll().forEach(tags::add);

    if (tags.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(tags, HttpStatus.OK);
  }
  
  @GetMapping("/tutorials/{tutorialId}/tags")
  public ResponseEntity<List<Tag>> getAllTagsByTutorialId(@PathVariable(value = "tutorialId") Long tutorialId) {
    if (!tutorialRepository.existsById(tutorialId)) {
      throw new ResourceNotFoundException("Not found Tutorial with id = " + tutorialId);
    }

    List<Tag> tags = tagRepository.findTagsByTutorialsId(tutorialId);
    return new ResponseEntity<>(tags, HttpStatus.OK);
  }

  @GetMapping("/tags/{id}")
  public ResponseEntity<Tag> getTagsById(@PathVariable(value = "id") Long id) {
    Tag tag = tagRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + id));

    return new ResponseEntity<>(tag, HttpStatus.OK);
  }
  
  @GetMapping("/tags/{tagId}/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorialsByTagId(@PathVariable(value = "tagId") Long tagId) {
    if (!tagRepository.existsById(tagId)) {
      throw new ResourceNotFoundException("Not found Tag  with id = " + tagId);
    }

    List<Tutorial> tutorials = tutorialRepository.findTutorialsByTagsId(tagId);
    return new ResponseEntity<>(tutorials, HttpStatus.OK);
  }

  @PostMapping("/tutorials/{tutorialId}/tags")
  public ResponseEntity<Tag> addTag(@PathVariable(value = "tutorialId") Long tutorialId, @RequestBody Tag tagRequest) {
    System.out.println("Tags Post"+tagRequest);
    Tag tag = tutorialRepository.findById(tutorialId).map(tutorial -> {
      System.out.println("Tutorials ::"+tutorial);
      long tagId = tagRequest.getId();
      System.out.println("Tag requestID ::"+tagId);

      // tag is existed
      if (tagId != 0L) {
        System.out.println("Inside tagId !=0L");
        Tag _tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + tagId));
        System.out.println("Tag::"+_tag);
        tutorial.addTag(_tag);
        System.out.println("Tag::added");
        tutorialRepository.save(tutorial);
        return _tag;
      }
      System.out.println("Outside if");
      // add and create new Tag
      tutorial.addTag(tagRequest);
      System.out.println("added");
//      return tagRepository.save(tagRequest);
      tutorialRepository.save(tutorial);
      return tagRequest;
    }).orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + tutorialId));
    System.out.println("Leaving "+tag);
    return new ResponseEntity<>(tag, HttpStatus.CREATED);
  }

  @PutMapping("/tags/{id}")
  public ResponseEntity<Tag> updateTag(@PathVariable("id") long id, @RequestBody Tag tagRequest) {
    Tag tag = tagRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("TagId " + id + "not found"));

    tag.setName(tagRequest.getName());

    return new ResponseEntity<>(tagRepository.save(tag), HttpStatus.OK);
  }
 
  @DeleteMapping("/tutorials/{tutorialId}/tags/{tagId}")
  public ResponseEntity<HttpStatus> deleteTagFromTutorial(@PathVariable(value = "tutorialId") Long tutorialId, @PathVariable(value = "tagId") Long tagId) {
    Tutorial tutorial = tutorialRepository.findById(tutorialId)
        .orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + tutorialId));
    
    tutorial.removeTag(tagId);
    tutorialRepository.save(tutorial);
    
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
  
  @DeleteMapping("/tags/{id}")
  public ResponseEntity<HttpStatus> deleteTag(@PathVariable("id") long id) {
    tagRepository.deleteById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
