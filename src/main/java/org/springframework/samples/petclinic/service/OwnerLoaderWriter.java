package org.springframework.samples.petclinic.service;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.spi.loaderwriter.BulkCacheLoadingException;
import org.ehcache.spi.loaderwriter.BulkCacheWritingException;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * @author Aurelien Broszniowski
 */
@Service
public class OwnerLoaderWriter implements CacheLoaderWriter<String, Collection> {

  private OwnerRepository ownerRepository;

  @Autowired
  public OwnerLoaderWriter(OwnerRepository ownerRepository) {
    this.ownerRepository = ownerRepository;
  }

  @Override
  public Collection load(final String name) throws Exception {
    System.out.println("--> it is not in the cache");
    Collection<Owner> owners = ownerRepository.findByLastName(name);
    return owners;
  }

  @Override
  public Map<String, Collection> loadAll(final Iterable<? extends String> iterable) throws BulkCacheLoadingException, Exception {
    return null;
  }

  @Override
  public void write(final String string, final Collection collection) throws Exception {

  }

  @Override
  public void writeAll(final Iterable<? extends Map.Entry<? extends String, ? extends Collection>> iterable) throws BulkCacheWritingException, Exception {

  }

  @Override
  public void delete(final String string) throws Exception {

  }

  @Override
  public void deleteAll(final Iterable<? extends String> iterable) throws Exception {

  }
}
