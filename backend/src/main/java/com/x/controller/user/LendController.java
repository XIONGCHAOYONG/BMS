package com.x.controller.user;

import com.x.pojo.dto.LendDTO;
import com.x.pojo.dto.ReLendDTO;
import com.x.pojo.dto.ReturnDTO;
import com.x.pojo.vo.LendVO;
import com.x.service.LendService;
import com.x.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/lend")
@Tag(name = "借阅接口")
public class LendController {
    @Autowired
    private LendService lendService;

    @GetMapping("/getAllLend")
    @Operation(summary = "获取所有借阅信息")
    public Result<List<LendVO>> getAllLend(){
        return Result.success(lendService.getAllLend());
    }

    @GetMapping("/getCurrentLend")
    @Operation(summary = "获取当前借阅信息")
    public  Result<List<LendVO>> getCurrentLend(){
        return Result.success(lendService.getCurrentLend());
    }

    @PostMapping("/lendBook")
    @Operation(summary = "借阅")
    public  Result lendBook(@RequestBody LendDTO lendDTO){
        lendService.lendBook(lendDTO);
        return Result.success();
    }

    @PutMapping("/renewBook")
    @Operation(summary = "续借")
    public  Result renewBook(@RequestBody ReLendDTO reLendDTO){
        lendService.renewBook(reLendDTO);
        return Result.success();
    }

    @PutMapping("/returnBook")
    @Operation(summary = "归还")
    public  Result returnBook(@RequestBody ReturnDTO returnDTO){
        lendService.returnBook(returnDTO);
        return Result.success();
    }

    @GetMapping("/getLendByBookId/{bookId}")
    @Operation(summary = "获取指定图书的借阅信息")
    public Result<List<LendVO>> getLendByBookId(@PathVariable Long bookId){
        return Result.success(lendService.getLendByBookId(bookId));
    }
}
