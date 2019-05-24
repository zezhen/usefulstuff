### big_query.hql

hive query from table `gemini_search_insights.auctions_trdd_v4`, which is central place to analyze the gemini search and bing ads performance.
this hql contains mainly useful dimentions and metrics, customize the query according to different scenarios.

### sw2v_clkb_bucket.hql

break down query-ads into 100 buckets according to click_probability, which is the clkb in adserver in runtime, so that we can know the clkb bias trend.

### sw2v_editorial.hql

query ads data for editorial judgement, usually they can done 5k per week, contract with manjuprasad@


