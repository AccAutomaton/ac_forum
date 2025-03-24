package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.CoinService;
import com.acautomaton.forum.service.UserService;
import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/coin")
public class CoinController {
    CoinService coinService;
    UserService userService;
    @Autowired
    public CoinController(CoinService coinService, UserService userService) {
        this.coinService = coinService;
        this.userService = userService;
    }

    @GetMapping("")
    public Response getCoinBalance() {
        Integer balance = coinService.getCoinsByUid(userService.getCurrentUser().getUid());
        return Response.success(Map.of("coins", balance));
    }

    @PostMapping("/buy")
    public Response buyCoins(@RequestParam Integer coins) throws AlipayApiException {
        if (coins <= 0 || coins > 99999999) {
            throw new ForumIllegalArgumentException("充值数非法");
        }
        return Response.success(Map.of("pageRedirectionData", coinService.buyCoins(userService.getCurrentUser().getUid(), coins)));
    }

    @PostMapping("/payed")
    public Response buyCoinsAfterPaying(@RequestParam String tradeId) throws AlipayApiException {
        coinService.afterPaying(userService.getCurrentUser().getUid(), tradeId);
        return Response.success();
    }

    @PostMapping("/refresh")
    public Response refreshPayingStatus() throws AlipayApiException {
        Boolean result = coinService.refreshPayingStatusByUid(userService.getCurrentUser().getUid());
        return Response.success(Map.of("hasNewStatus", result));
    }

    @GetMapping("/record/list")
    public Response getCoinRecordList(@RequestParam Integer pageNumber,
                                      @RequestParam Integer pageSize) {
        return Response.success(coinService.getCoinRecordList(userService.getCurrentUser().getUid(), pageNumber, pageSize));
    }

    @GetMapping("/record/{coinRecordId}")
    public Response getCoinRecord(@PathVariable Integer coinRecordId) {
        return Response.success(coinService.getCoinRecordById(coinRecordId, userService.getCurrentUser().getUid()));
    }
}
