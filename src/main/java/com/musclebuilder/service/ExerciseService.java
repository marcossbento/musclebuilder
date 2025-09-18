package com.musclebuilder.service;

import com.musclebuilder.dto.ExerciseDTO;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.model.Exercise;
import com.musclebuilder.model.MuscleGroup;
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
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado com id: " +id));

        return convertToDTO(exercise);
    }

    public List<ExerciseDTO> getAllExercises() {
        return exerciseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ExerciseDTO> getExerciseByMuscleGroup(MuscleGroup muscleGroup) {
        return exerciseRepository.findByMuscleGroup(muscleGroup).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ExerciseDTO> searchExercisesByName(String name) {
        return exerciseRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExerciseDTO updateExercise(Long id, ExerciseDTO exerciseDTO) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não econtrado com id: " + id));

        exercise.setName(exerciseDTO.name());
        exercise.setDescription(exerciseDTO.description());
        exercise.setMuscleGroup(exerciseDTO.muscleGroup());
        exercise.setEquipment(exerciseDTO.equipment());
        exercise.setDifficultyLevel(exerciseDTO.difficultyLevel());
        exercise.setImageUrl(exerciseDTO.imageUrl());

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
        return new ExerciseDTO(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getMuscleGroup(),
                exercise.getEquipment(),
                exercise.getDifficultyLevel(),
                exercise.getImageUrl()
        );
    }

    private Exercise convertToEntity(ExerciseDTO dto) {
        Exercise exercise = new Exercise();
        exercise.setName(dto.name());
        exercise.setDescription(dto.description());
        exercise.setMuscleGroup(dto.muscleGroup());
        exercise.setEquipment(dto.equipment());
        exercise.setDifficultyLevel(dto.difficultyLevel());
        exercise.setImageUrl(dto.imageUrl());
        return exercise;
    }

}
