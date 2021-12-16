phonesPerPrefix = function () {
  return db.phones.aggregate([{$group: {_id: "$components.prefix", counter: {$sum: 1}}}, {$sort: {counter: -1}}])
}
