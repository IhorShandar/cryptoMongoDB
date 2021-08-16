package com.cryptomongodb.dao;

import com.cryptomongodb.models.Crypto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CryptoDAO extends MongoRepository<Crypto, String> {

    List<Crypto> findAllByCurr1(String curr1, Pageable pageable);

    Crypto findTopByCurr1OrderByLpriceAsc(String curr1);

    Crypto findTopByCurr1OrderByLpriceDesc(String curr1);

    Crypto findFirstByCurr1AndCurr2OrderByIDDesc(String curr1, String curr2);

}
