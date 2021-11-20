package com.tutorial.userservice.service;

import com.tutorial.userservice.entity.User;
import com.tutorial.userservice.feignclients.BikeFeignClient;
import com.tutorial.userservice.feignclients.CarFeignClient;
import com.tutorial.userservice.model.Bike;
import com.tutorial.userservice.model.Car;
import com.tutorial.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CarFeignClient carFeignClient;

	@Autowired
	BikeFeignClient bikeFeignClient;

	public List<User> getAll() {
		return userRepository.findAll();
	}

	public User getUserById(int id) {
		return userRepository.findById(id).orElse(null);
	}

	public User save(User user) {
		User userNew = userRepository.save(user);
		return userNew;
	}

	public Car saveCar(int userId, Car car) {
		car.setUserId(userId);
		return carFeignClient.save(car);
	}

	public List<Car> carsByUserId(int userId) {
		return carFeignClient.getByUserId(userId);
	}

	public Bike saveBike(int userId, Bike bike) {
		bike.setUserId(userId);
		return bikeFeignClient.save(bike);
	}

	public List<Bike> bikesByUserId(int userId) {
		return bikeFeignClient.getByUserId(userId);
	}

	public Map<String, Object> getUserAndVehicles(int userId) {
		Map<String, Object> map = new HashMap<>();

		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			map.put("User", "El usuario no existe");
			return map;
		} else {
			map.put("User", user);
			List<Car> cars = carFeignClient.getByUserId(userId);

			if (cars == null || cars.isEmpty()) {
				map.put("Cars", "El usuario no tiene coche");
			}else {
				map.put("Cars", cars);
			}

			List<Bike> bikes = bikeFeignClient.getByUserId(userId);

			if (bikes == null || bikes.isEmpty()) {
				map.put("Bikes", "El usuario no tiene moto");
			}else {
				map.put("Bikes", bikes);
			}

		}
		return map;

	}
}

