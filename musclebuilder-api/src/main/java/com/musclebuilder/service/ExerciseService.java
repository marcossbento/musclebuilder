package com.musclebuilder.service;

import com.musclebuilder.dto.ExerciseDTO;
import com.musclebuilder.model.Exercise;
import com.musclebuilder.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public ExerciseDTO createExercise(ExerciseDTO exerciseDTO) {
        Exercise exercise = convertToEntity(exerciseDTO);
        Exercise saverExercise = exerciseRepository.save(exercise);
        return convertToDTO(saverExercise);
    }

    public ExerciseDTO getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById()
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado com id: " +id));

        return convertToDTO(exercise);
    }

    public List<ExerciseDTO> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExerciseDTO updateExercise(Long id, ExerciseDTO exerciseDTO) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não econtrado com id: " + id));

        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setMuscleGroup(exerciseDTO.getMuscleGroup());
        exercise.setEquipment(exerciseDTO.getEquipment());
        exercise.setDifficultyLevel(exerciseDTO.getDifficultyLevel());
        exercise.setImageUrl(exerciseDTO.getImageUrl());

        Exercise updatedExercise = exerciseRepository.save(exercise);
        return convertToDTO(updatedExercise);
    }

    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exercício não encontrado com id: " + id);
        }

        exerciseRepository.deleteById(id);
    }


    private ExerciseDTO convertToDTO(Exercise exercise) {
        ExerciseDTO dto = new ExerciseDTO();
        dto.setId(exercise.getId());
        dto.setName(exercise.getName());
        dto.setDescription(exercise.getDescription());
        dto.setMuscleGroup(exercise.getMuscleGroup());
        dto.setEquipment(exercise.getEquipment());
        dto.setDifficultyLevel(exercise.getDifficultyLevel());
        dto.setImageUrl(exercise.getImageUrl());
        return dto;
    }

    private Exercise convertToEntity(ExerciseDTO dto) {
        Exercise exercise = new Exercise();
        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setMuscleGroup(dto.getMuscleGroup());
        exercise.setEquipment(dto.getEquipment());
        exercise.setDifficultyLevel(dto.getDifficultyLevel());
        exercise.setImageUrl(dto.getImageUrl());
        return exercise;
    }
}
