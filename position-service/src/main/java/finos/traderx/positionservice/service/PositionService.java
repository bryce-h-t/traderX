package finos.traderx.positionservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import finos.traderx.positionservice.model.*;
import finos.traderx.positionservice.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionService {

	@Autowired
	PositionRepository positionRepository;

	public List<Position> getAllPositions() {
		try {
			List<Position> positions = new ArrayList<Position>();
			this.positionRepository.findAll().forEach(account -> positions.add(account));
			return positions;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public List<Position> getPositionsByAccountID(int id) {
		try {
			return this.positionRepository.findByAccountId(id);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
