package com.nexora.xatu.chansey.weight.repository;

import com.nexora.xatu.chansey.weight.model.WeightEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface WeightEntryRepository extends MongoRepository<WeightEntry, String> {

  @Query(
      """
      {
        'userId': ?0,
        $expr: {
          $eq: [
            { $dateToString: { format: '%Y-%m-%d', date: '$recordedOn', timezone: 'UTC' } },
            ?1
          ]
        }
      }
      """)
  Optional<WeightEntry> findByUserIdAndRecordedOnDate(String userId, String recordedOn);

  Optional<WeightEntry> findTopByUserIdOrderByRecordedOnDescRecordedAtDesc(String userId);

  @Query(
      value =
          """
          {
            'userId': ?0,
            $expr: {
              $and: [
                {
                  $gte: [
                    { $dateToString: { format: '%Y-%m-%d', date: '$recordedOn', timezone: 'UTC' } },
                    ?1
                  ]
                },
                {
                  $lte: [
                    { $dateToString: { format: '%Y-%m-%d', date: '$recordedOn', timezone: 'UTC' } },
                    ?2
                  ]
                }
              ]
            }
          }
          """,
      sort = "{ 'recordedOn': 1 }")
  List<WeightEntry> findByUserIdAndRecordedOnInRange(
      String userId, String fromInclusive, String toInclusive);

  Optional<WeightEntry> findByIdAndUserId(String id, String userId);
}
