package com.tutorial.userservice.controller;

import com.tutorial.userservice.entity.User;
import com.tutorial.userservice.model.Bike;
import com.tutorial.userservice.model.Car;
import com.tutorial.userservice.service.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        if(users.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        if(user == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    public ResponseEntity<User> save(@RequestBody User user) {
        User userNew = userService.save(user);
        return ResponseEntity.ok(userNew);
    }
    
    //metodos protegidos
    
    @CircuitBreaker(name = "carsCB", fallbackMethod = "fallbackSaveCar")
    @PostMapping("/savecar/{userId}")
    public ResponseEntity<Car> saveCar(@PathVariable("userId") int userId, @RequestBody Car car) {
        Car carNew = userService.saveCar(userId, car);
        return ResponseEntity.ok(carNew);
    }
    
    @CircuitBreaker(name = "carsCB", fallbackMethod = "fallbackGetCars")
    @GetMapping("/cars/{userId}")
    public ResponseEntity<List<Car>> getCars(@PathVariable("userId") int userId) {
    	List<Car> cars = userService.carsByUserId(userId);
    	return ResponseEntity.ok(cars);
    	
    }

    @CircuitBreaker(name = "bikesCB", fallbackMethod = "fallbackSaveBike")
    @PostMapping("/savebike/{userId}")
    public ResponseEntity<Bike> saveBike(@PathVariable("userId") int userId, @RequestBody Bike bike) {
    	Bike bikeNew = userService.saveBike(userId, bike);
        return ResponseEntity.ok(bikeNew);
    }
    
    @CircuitBreaker(name = "bikesCB", fallbackMethod = "fallbackGetBikes")
    @GetMapping("/bikes/{userId}")
    public ResponseEntity<List<Bike>> getBikes(@PathVariable("userId") int userId) {
    	List<Bike> bikes = userService.bikesByUserId(userId);
    	return ResponseEntity.ok(bikes);
    	
    }
    
    @CircuitBreaker(name = "allCB", fallbackMethod = "fallbackGetAll")
    @GetMapping("/getAll/{userId}")
    public ResponseEntity<Map<String, Object>> getAllVehicles(@PathVariable("userId") int userId) {
    	Map<String, Object> vehicles = userService.getUserAndVehicles(userId);
    	return ResponseEntity.ok(vehicles);
    	
    }
    
    //fallbacks
    
    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	private ResponseEntity<Car> fallbackSaveCar(@PathVariable("userId") int userId, @RequestBody Car car, RuntimeException e){
    	return new ResponseEntity("El usuario "+userId+" no se puede permitir un coche", HttpStatus.OK);
    }
    
    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    private ResponseEntity<List<Car>> fallbackGetCars(@PathVariable("userId") int userId, RuntimeException e){
    	return new ResponseEntity("El usuario "+userId+" tiene los coches en el taller", HttpStatus.OK);
    }
    
    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    private ResponseEntity<Bike> fallbackSaveBike(@PathVariable("userId") int userId, @RequestBody Bike bike, RuntimeException e){
    	return new ResponseEntity("El usuario "+userId+" no se puede permitir una moto", HttpStatus.OK);
    }
    
    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    private ResponseEntity<List<Bike>> fallbackGetBikes(@PathVariable("userId") int userId, RuntimeException e){
    	return new ResponseEntity("El usuario "+userId+" tiene las motos en el taller", HttpStatus.OK);
    }
    
    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    private ResponseEntity<Map<String, Object>> fallbackGetAll(@PathVariable("userId") int userId, RuntimeException e){
    	return new ResponseEntity("El usuario "+userId+" tiene TODOS los vehiculos en el taller", HttpStatus.OK);
    }
    
    
    
}
