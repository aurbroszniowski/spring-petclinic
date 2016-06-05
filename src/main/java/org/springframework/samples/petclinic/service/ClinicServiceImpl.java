/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.service;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Mostly used as a facade for all Petclinic controllers Also a placeholder for @Transactional
 * and @Cacheable annotations
 *
 * @author Michael Isvy
 */
@Service
@Transactional
public class ClinicServiceImpl implements ClinicService {

	private static final Logger LOGGER = LoggerFactory.getLogger("org.ehcache.Demo");


	private final Cache<String, Collection> ownersSearchCache;
	private PetRepository petRepository;
	private VetRepository vetRepository;
	private OwnerRepository ownerRepository;
	private VisitRepository visitRepository;

	@Autowired
	public ClinicServiceImpl(PetRepository petRepository, VetRepository vetRepository,
													 OwnerRepository ownerRepository, VisitRepository visitRepository,
													 CacheManager ehcacheManager) {
		this.petRepository = petRepository;
		this.vetRepository = vetRepository;
		this.ownerRepository = ownerRepository;
		this.visitRepository = visitRepository;
		ownersSearchCache = ehcacheManager.getCache("ownersSearch", String.class, Collection.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<PetType> findPetTypes() throws DataAccessException {
		return this.petRepository.findPetTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public Owner findOwnerById(int id) throws DataAccessException {
		return this.ownerRepository.findById(id);
	}

  @Override
  @Transactional(readOnly = true)
  public Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException {
    System.out.println("--> search Owner by name");
    return ownersSearchCache.get(lastName);
  }

	@Override
	public void saveOwner(Owner owner) throws DataAccessException {
    System.out.println("--> save Owner");
    ownersSearchCache.put(owner.getLastName(), Arrays.asList(owner));
	}

	@Override
	public void saveVisit(Visit visit) throws DataAccessException {
		this.visitRepository.save(visit);
	}

	@Override
	public Pet findPetById(int id) throws DataAccessException {
		return this.petRepository.findById(id);
	}

	@Override
	public void savePet(Pet pet) throws DataAccessException {
		this.petRepository.save(pet);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "vets")
	public Collection<Vet> findVets() throws DataAccessException {
		return this.vetRepository.findAll();
	}

}
