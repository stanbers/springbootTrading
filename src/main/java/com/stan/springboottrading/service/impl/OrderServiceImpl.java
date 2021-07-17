package com.stan.springboottrading.service.impl;

import com.stan.springboottrading.dataobject.OrderDetail;
import com.stan.springboottrading.dataobject.OrderMaster;
import com.stan.springboottrading.dataobject.ProductInfo;
import com.stan.springboottrading.dto.CartDTO;
import com.stan.springboottrading.dto.OrderDTO;
import com.stan.springboottrading.enums.OrderStatusEnum;
import com.stan.springboottrading.enums.PayStatusEnum;
import com.stan.springboottrading.enums.ResultEnum;
import com.stan.springboottrading.exception.SellException;
import com.stan.springboottrading.repository.OrderDetailRespository;
import com.stan.springboottrading.repository.OrderMasterResposity;
import com.stan.springboottrading.service.OrderService;
import com.stan.springboottrading.service.ProductService;
import com.stan.springboottrading.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailRespository orderDetailRespository;

    @Autowired
    private OrderMasterResposity orderMasterResposity;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        //init order amount
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);

        String orderId = KeyUtil.genUniqueKey();

        //query product that you buy:
       for(OrderDetail orderDetail: orderDTO.getOrderDetailList()){
           Optional<ProductInfo> productInfo = productService.findById(orderDetail.getProductId());
           if(productInfo == null){
                throw new SellException(ResultEnum.PRODDUCT_NOT_EXSIT);
           }
           //calculate total price
           orderAmount = productInfo.get().getProductPrice().multiply(new BigDecimal(orderDetail.getProductQuantity())).add(orderAmount);

           orderDetail.setOrderId(orderId);
           orderDetail.setDetailId(KeyUtil.genUniqueKey() );
           BeanUtils.copyProperties(productInfo.get(),orderDetail);

           //save to order detail table
           orderDetailRespository.save(orderDetail);
       }
        //save to order master table
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderId(orderId);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
           orderMasterResposity.save(orderMaster);

        //decrease inventory
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream().map(e -> new CartDTO(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.decreaseStock(cartDTOList);

        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {
        return null;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        return null;
    }

    @Override
    public OrderDTO cancel(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public OrderDTO finish(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public OrderDTO paid(OrderDTO orderDTO) {
        return null;
    }
}