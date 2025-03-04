
/*
 * Copyright 2018-2022 WangSheng.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hhao.common.dto;

/**
 * 分页查询条件查询对象
 *
 * @author Wang
 * @since 2022/2/22 21:54
 */
public class PageQuery extends Query implements Page{
    private static final long serialVersionUID = 1L;

    private long pageNum;
    private long pageSize;
    private long preCachedPage;
    private long postCachedPage;
    private boolean includeTotalRows=true;
    private OrderDirection orderDirection=OrderDirection.ASC;
    private String [] orderColumns;

    @Override
    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    @Override
    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public long getPreCachedPage() {
        return preCachedPage;
    }

    public void setPreCachedPage(long preCachedPage) {
        this.preCachedPage = preCachedPage;
    }

    @Override
    public long getPostCachedPage() {
        return postCachedPage;
    }

    public void setPostCachedPage(long postCachedPage) {
        this.postCachedPage = postCachedPage;
    }

    @Override
    public boolean isIncludeTotalRows() {
        return includeTotalRows;
    }

    public void setIncludeTotalRows(boolean includeTotalRows) {
        this.includeTotalRows = includeTotalRows;
    }

    @Override
    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public String[] getOrderColumns() {
        return orderColumns;
    }

    public void setOrderColumns(String[] orderColumns) {
        this.orderColumns = orderColumns;
    }
}
