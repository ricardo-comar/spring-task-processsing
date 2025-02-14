package com.rhcsoft.spring.task.pool.taskpooldemo.repo;

import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import com.rhcsoft.spring.task.pool.taskpooldemo.document.EventDoc;

@Repository
public interface EventDocRepository extends CouchbaseRepository<EventDoc, String> {

}
