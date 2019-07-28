package com.maxwang.buycar.service.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.maxwang.buycar.dto.CarSearch;
import com.maxwang.buycar.redis.RedisService;
import com.maxwang.buycar.util.ServiceResult;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {

    private static final String INDEX_NAME = "auto";

    private static final String INDEX_TYPE = "car";

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private TransportClient client;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 增加索引
     */
    public boolean create(CarIndexTemplate carIndexTemplate){

        if (!updateSuggest(carIndexTemplate)){
            return false;
        }

        try {
            IndexResponse response = this.client.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(carIndexTemplate), XContentType.JSON).get();
            logger.debug("Create index with car: " + carIndexTemplate.getCarId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index car " + carIndexTemplate.getCarId(), e);
            return false;
        }
    }

    /**
     * 删除索引
     * @param carId
     */
    public void delete(Integer carId){

        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.termQuery(CarIndexKey.CAR_ID, carId))
                .source(INDEX_NAME);

        logger.debug("Delete by query for car: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.debug("Delete total " + deleted);
    }

    /**
     * 更新索引
     * @param carIndexTemplate
     * @return
     */
    public boolean update(CarIndexTemplate carIndexTemplate){

        if (!updateSuggest(carIndexTemplate)){
            return false;
        }

        Integer carId = carIndexTemplate.getCarId();

        SearchRequestBuilder requestBuilder = this.client.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(CarIndexKey.CAR_ID, carId));

        SearchResponse searchResponse = requestBuilder.get();

        String esId = searchResponse.getHits().getAt(0).getId();

        try {
            UpdateResponse response = this.client.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId).setDoc(objectMapper.writeValueAsBytes(carIndexTemplate), XContentType.JSON).get();

            logger.debug("Update index with car: " + carIndexTemplate.getCarId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Error to index car " + carIndexTemplate.getCarId(), e);
            return false;
        }
    }

    /**
     * 搜索
     * @param carSearch
     * @return
     */
    public List<Map<String, Object>>  query(CarSearch carSearch){

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


        boolQuery.must(
                QueryBuilders.multiMatchQuery(carSearch.getKeywords(),
                        CarIndexKey.FIRM,
                        CarIndexKey.NAME,
                        CarIndexKey.TAGS,
                        CarIndexKey.ENERGY,
                        CarIndexKey.STRUCTURE
                ));


        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(CarIndexKey.PRICE);

        if (carSearch.getPrice()== 0){
            rangeQuery.gt(0.0);
        }else if (carSearch.getPrice()== 8){
            rangeQuery.gt(0.0);
            rangeQuery.lte(8.0);
        }else if (carSearch.getPrice()== 12){
            rangeQuery.gt(8.0);
            rangeQuery.lte(12.0);
        }else if (carSearch.getPrice()== 18){
            rangeQuery.gt(12.0);
            rangeQuery.lte(18.0);
        }else if (carSearch.getPrice()== 25){
            rangeQuery.gt(18.0);
            rangeQuery.lte(25.0);
        }else if (carSearch.getPrice()== 40){
            rangeQuery.gt(25.0);
            rangeQuery.lte(40.0);
        }else if (carSearch.getPrice()== 80){
            rangeQuery.gt(40.0);
            rangeQuery.lte(80.0);
        }else {
            rangeQuery.gt(80.0);
        }
        boolQuery.filter(rangeQuery);

        if (!("不限".equals(carSearch.getStructure()))){
            boolQuery.must(
                    QueryBuilders.termQuery(CarIndexKey.STRUCTURE, carSearch.getStructure())
            );
        }

        if (!("不限".equals(carSearch.getEnergy()))){
            logger.info("..........carSearch.getEnergy()........"+carSearch.getEnergy());
            boolQuery.must(
                    QueryBuilders.termQuery(CarIndexKey.ENERGY, carSearch.getEnergy())
            );
        }

        System.out.println(boolQuery.toString());
        SearchRequestBuilder requestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery);


        logger.debug(requestBuilder.toString());
        SearchResponse response = requestBuilder.get();


        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : response.getHits()){
            result.add(hit.getSourceAsMap());
        }

        return result;
    }


    /**
     * 对索引中的suggest进行填充
     * @param indexTemplate
     * @return
     */
    private boolean updateSuggest(CarIndexTemplate indexTemplate) {
        /*AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(
                this.client, AnalyzeAction.INSTANCE, INDEX_NAME, indexTemplate.getName(),
                indexTemplate.getFirm(), indexTemplate.getStructure());

        requestBuilder.setAnalyzer("ik_smart");

        AnalyzeResponse response = requestBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        if (tokens == null) {
            logger.warn("Can not analyze token for house: " + indexTemplate.getCarId());
            return false;
        }

        List<CarSuggest> suggests = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // 排序数字类型 & 小于2个字符的分词结果
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }

            CarSuggest suggest = new CarSuggest();
            suggest.setInput(token.getTerm());
            suggests.add(suggest);
        }
*/
        List<CarSuggest> suggests = new ArrayList<>();

        // 定制化汽车厂商自动补全,为keyword的不能分词的
        CarSuggest suggest1 = new CarSuggest();
        suggest1.setInput(indexTemplate.getFirm());

        //定制化汽车名自动补全
        CarSuggest suggest2 = new CarSuggest();
        suggest2.setInput(indexTemplate.getName());

        //定制化汽车结构自动补全
        CarSuggest suggest3 = new CarSuggest();
        suggest3.setInput(indexTemplate.getStructure());

        //定制化汽车能源自动补全
        CarSuggest suggest4 = new CarSuggest();
        suggest4.setInput(indexTemplate.getEnergy());

        suggests.add(suggest1);
        suggests.add(suggest2);
        suggests.add(suggest3);
        suggests.add(suggest4);

        indexTemplate.setSuggest(suggests);
        return true;
    }


    /**
     * 关键词填充接口
     * @param prefix
     * @return
     */
    public ServiceResult<List<String>> suggest(String prefix) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("autocomplete", suggestion);

        SearchRequestBuilder requestBuilder = this.client.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .suggest(suggestBuilder);

        logger.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        Suggest suggest = response.getSuggest();
        if (suggest == null) {
            return ServiceResult.of(new ArrayList<>());
        }
        Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

        int maxSuggest = 0;
        Set<String> suggestSet = new HashSet<>();

        for (Object term : result.getEntries()) {
            if (term instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;

                if (item.getOptions().isEmpty()) {
                    continue;
                }

                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    if (suggestSet.contains(tip)) {
                        continue;
                    }
                    suggestSet.add(tip);
                    maxSuggest++;
                }
            }

            if (maxSuggest > 5) {
                break;
            }
        }

        List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
        return ServiceResult.of(suggests);
    }
}
